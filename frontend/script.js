let authToken = null;
let currentPuzzle = null;

// Supabase configuration - REPLACE WITH YOUR VALUES
const SUPABASE_URL = 'https://YOUR_PROJECT.supabase.co';
const SUPABASE_ANON_KEY = 'YOUR_SUPABASE_ANON_KEY';
const supabase = supabase.createClient(SUPABASE_URL, SUPABASE_ANON_KEY);

const API_BASE = 'http://localhost:8080/api';

async function register() {
    const username = document.getElementById('regUsername').value;
    const email = document.getElementById('regEmail').value;
    const password = document.getElementById('regPassword').value;
    
    if (!username || !email || !password) {
        addConsoleMessage('Please fill all fields!', 'error');
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, email, password })
        });
        
        const data = await response.json();
        
        if (response.ok) {
            addConsoleMessage('Registration successful! Please login.', 'success');
            showLogin();
        } else {
            addConsoleMessage(data.error || 'Registration failed!', 'error');
        }
    } catch (error) {
        addConsoleMessage('Network error!', 'error');
    }
}

async function login() {
    const username = document.getElementById('loginUsername').value;
    const password = document.getElementById('loginPassword').value;
    
    if (!username || !password) {
        addConsoleMessage('Please enter username and password!', 'error');
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });
        
        const data = await response.json();
        
        if (response.ok) {
            authToken = data.token;
            localStorage.setItem('token', authToken);
            localStorage.setItem('username', data.username);
            await loadGameData();
            showGame();
            addConsoleMessage(`Welcome back, ${data.username}! Access granted.`, 'success');
        } else {
            addConsoleMessage(data.error || 'Login failed!', 'error');
        }
    } catch (error) {
        addConsoleMessage('Network error!', 'error');
    }
}

async function supabaseLogin() {
    try {
        // Show Supabase OAuth popup
        const { data, error } = await supabase.auth.signInWithOAuth({
            provider: 'github' // Can be 'google', 'github', 'twitter', etc.
        });
        
        if (error) throw error;
        
        // Listen for auth state change
        supabase.auth.onAuthStateChange(async (event, session) => {
            if (event === 'SIGNED_IN' && session) {
                const response = await fetch(`${API_BASE}/auth/supabase-login`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ supabaseToken: session.access_token })
                });
                
                const data = await response.json();
                
                if (response.ok) {
                    authToken = data.token;
                    localStorage.setItem('token', authToken);
                    localStorage.setItem('username', data.username);
                    await loadGameData();
                    showGame();
                    addConsoleMessage(`Welcome, ${data.username}! Authenticated via Supabase.`, 'success');
                }
            }
        });
    } catch (error) {
        addConsoleMessage('Supabase login failed: ' + error.message, 'error');
    }
}

async function loadGameData() {
    try {
        const response = await fetch(`${API_BASE}/game/profile`, {
            headers: { 'Authorization': `Bearer ${authToken}` }
        });
        
        const data = await response.json();
        
        document.getElementById('playerUsername').textContent = data.username;
        document.getElementById('playerLevel').textContent = data.level;
        document.getElementById('playerXp').textContent = data.xp;
        document.getElementById('nextLevelXp').textContent = data.level * 500;
        document.getElementById('playerCoins').textContent = data.coins;
        document.getElementById('vaultLevel').textContent = data.vaultLevel;
        document.getElementById('successfulHacks').textContent = data.successfulHacks;
        
        await loadLeaderboard();
    } catch (error) {
        addConsoleMessage('Failed to load game data!', 'error');
    }
}

async function loadLeaderboard() {
    try {
        const response = await fetch(`${API_BASE}/game/leaderboard`);
        const data = await response.json();
        
        const leaderboardList = document.getElementById('leaderboardList');
        leaderboardList.innerHTML = '';
        
        data.leaderboard.forEach((player, index) => {
            const item = document.createElement('div');
            item.className = 'leaderboard-item';
            item.innerHTML = `
                <span>${index + 1}. ${player.username}</span>
                <span>LVL ${player.level} | ${player.xp} XP</span>
            `;
            leaderboardList.appendChild(item);
        });
    } catch (error) {
        console.error('Failed to load leaderboard:', error);
    }
}

async function getNewPuzzle() {
    try {
        const response = await fetch(`${API_BASE}/game/puzzle`, {
            headers: { 'Authorization': `Bearer ${authToken}` }
        });
        
        const data = await response.json();
        
        if (response.ok) {
            currentPuzzle = data;
            document.getElementById('puzzleType').textContent = data.type;
            document.getElementById('puzzleReward').textContent = data.reward;
            document.getElementById('puzzleQuestion').innerHTML = `<div class="cursor-blink">>_</div> ${data.question}`;
            document.getElementById('puzzleAnswer').disabled = false;
            document.getElementById('submitBtn').disabled = false;
            addConsoleMessage(`New ${data.type} puzzle generated! Solve it to earn ${data.reward} XP.`, 'info');
        } else {
            addConsoleMessage(data.error || 'Failed to generate puzzle!', 'error');
        }
    } catch (error) {
        addConsoleMessage('Network error!', 'error');
    }
}

async function submitAnswer() {
    if (!currentPuzzle) {
        addConsoleMessage('Generate a puzzle first!', 'error');
        return;
    }
    
    const answer = document.getElementById('puzzleAnswer').value;
    
    if (!answer) {
        addConsoleMessage('Enter your answer!', 'error');
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/game/verify`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${authToken}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                answer: answer,
                puzzleType: currentPuzzle.type,
                reward: currentPuzzle.reward
            })
        });
        
        const data = await response.json();
        
        if (data.success) {
            addConsoleMessage(data.message, 'success');
            await loadGameData();
            document.getElementById('puzzleAnswer').value = '';
            currentPuzzle = null;
            document.getElementById('puzzleAnswer').disabled = true;
            document.getElementById('submitBtn').disabled = true;
            document.getElementById('puzzleQuestion').innerHTML = '<div class="cursor-blink">>_</div> Puzzle solved! Generate a new one to continue hacking.';
        } else {
            addConsoleMessage(data.message || 'Wrong answer! Try again.', 'error');
        }
    } catch (error) {
        addConsoleMessage('Network error!', 'error');
    }
}

async function upgradeVault() {
    try {
        const response = await fetch(`${API_BASE}/game/upgrade-vault`, {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${authToken}` }
        });
        
        const data = await response.json();
        
        if (data.success) {
            addConsoleMessage(data.message, 'success');
            await loadGameData();
        } else {
            addConsoleMessage(data.message, 'error');
        }
    } catch (error) {
        addConsoleMessage('Network error!', 'error');
    }
}

function addConsoleMessage(message, type = 'info') {
    const consoleMessages = document.getElementById('consoleMessages');
    const messageDiv = document.createElement('div');
    messageDiv.className = `console-message ${type}`;
    messageDiv.innerHTML = `> ${new Date().toLocaleTimeString()} - ${message}`;
    consoleMessages.appendChild(messageDiv);
    messageDiv.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    
    // Auto-scroll
    consoleMessages.scrollTop = consoleMessages.scrollHeight;
}

function showGame() {
    document.getElementById('loginPage').classList.remove('active');
    document.getElementById('registerPage').classList.remove('active');
    document.getElementById('gamePage').classList.add('active');
}

function showLogin() {
    document.getElementById('loginPage').classList.add('active');
    document.getElementById('registerPage').classList.remove('active');
    document.getElementById('gamePage').classList.remove('active');
}

function showRegister() {
    document.getElementById('loginPage').classList.remove('active');
    document.getElementById('registerPage').classList.add('active');
    document.getElementById('gamePage').classList.remove('active');
}

function logout() {
    authToken = null;
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    
    // Also sign out from Supabase
    supabase.auth.signOut();
    
    showLogin();
    addConsoleMessage('Disconnected from system. H4CK TH3 PL4N3T!', 'info');
}

// Check for existing session
window.onload = () => {
    const savedToken = localStorage.getItem('token');
    if (savedToken) {
        authToken = savedToken;
        loadGameData();
        showGame();
    } else {
        showLogin();
    }
    
    // Check Supabase session
    supabase.auth.getSession().then(({ data: { session } }) => {
        if (session) {
            console.log('Supabase session active');
        }
    });
};