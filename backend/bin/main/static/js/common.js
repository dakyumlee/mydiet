function showLoading(show = true) {
    const loading = document.getElementById('loading');
    if (loading) {
        loading.classList.toggle('hidden', !show);
    }
}

function showMessage(message, type = 'success') {
    const messageEl = document.getElementById('message');
    if (messageEl) {
        messageEl.textContent = message;
        messageEl.className = `message ${type}`;
        messageEl.classList.remove('hidden');
        setTimeout(() => messageEl.classList.add('hidden'), 3000);
    }
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });
}

function formatTime(dateString) {
    const date = new Date(dateString);
    return date.toLocaleTimeString('ko-KR', {
        hour: '2-digit',
        minute: '2-digit'
    });
}

function formatDateTime(dateString) {
    return `${formatDate(dateString)} ${formatTime(dateString)}`;
}

async function apiCall(url, options = {}) {
    try {
        const response = await fetch(url, {
            ...options,
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            }
        });
        
        const data = await response.json();
        return { success: response.ok, data };
    } catch (error) {
        console.error('API call failed:', error);
        return { success: false, error: error.message };
    }
}

async function logout() {
    try {
        await fetch('/api/auth/logout', { method: 'POST' });
        showMessage('로그아웃되었습니다.');
        setTimeout(() => {
            window.location.href = '/login.html';
        }, 1000);
    } catch (error) {
        showMessage('로그아웃 중 오류가 발생했습니다.', 'error');
    }
}

async function checkAuth() {
    try {
        const response = await fetch('/api/auth/me');
        if (!response.ok) {
            window.location.href = '/login.html';
            return null;
        }
        const data = await response.json();
        return data.success ? data.user : null;
    } catch (error) {
        window.location.href = '/login.html';
        return null;
    }
}

document.addEventListener('DOMContentLoaded', function() {
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', logout);
    }
    
    const adminLogout = document.getElementById('adminLogout');
    if (adminLogout) {
        adminLogout.addEventListener('click', logout);
    }
    
    document.querySelectorAll('.modal-close').forEach(btn => {
        btn.addEventListener('click', function() {
            this.closest('.modal').classList.add('hidden');
        });
    });
    
    document.querySelectorAll('.modal').forEach(modal => {
        modal.addEventListener('click', function(e) {
            if (e.target === this) {
                this.classList.add('hidden');
            }
        });
    });
});

function formatNumber(num, decimals = 1) {
    if (num == null) return '-';
    return parseFloat(num).toFixed(decimals);
}

function calculateCalories(weight, duration, activity) {
    const MET = {
        '걷기': 3.8,
        '뛰기': 8.0,
        '자전거': 6.8,
        '수영': 7.0,
        '헬스': 5.0,
        '요가': 2.5
    };
    
    const met = MET[activity] || 4.0;
    return Math.round(met * weight * (duration / 60));
}

function calculateWeightChange(current, previous) {
    if (!current || !previous) return null;
    const change = current - previous;
    const changeText = change > 0 ? `+${formatNumber(change)}kg` : `${formatNumber(change)}kg`;
    const changeClass = change > 0 ? 'weight-gain' : (change < 0 ? 'weight-loss' : 'weight-same');
    return { change, changeText, changeClass };
}

function calculateDaysToGoal(currentWeight, goalWeight, avgWeeklyLoss) {
    if (!currentWeight || !goalWeight || !avgWeeklyLoss || avgWeeklyLoss <= 0) {
        return null;
    }
    
    const remainingWeight = currentWeight - goalWeight;
    if (remainingWeight <= 0) return 0;
    
    const weeksToGoal = remainingWeight / avgWeeklyLoss;
    return Math.ceil(weeksToGoal * 7);
}

function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

const storage = {
    set: (key, value) => {
        try {
            localStorage.setItem(key, JSON.stringify(value));
        } catch (error) {
            console.error('Storage set failed:', error);
        }
    },
    
    get: (key, defaultValue = null) => {
        try {
            const item = localStorage.getItem(key);
            return item ? JSON.parse(item) : defaultValue;
        } catch (error) {
            console.error('Storage get failed:', error);
            return defaultValue;
        }
    },
    
    remove: (key) => {
        try {
            localStorage.removeItem(key);
        } catch (error) {
            console.error('Storage remove failed:', error);
        }
    }
};

const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
        legend: {
            position: 'top',
        }
    },
    scales: {
        y: {
            beginAtZero: false
        }
    }
};

const emotionEmojis = {
    '행복': '😊',
    '우울': '😢',
    '스트레스': '😫',
    '분노': '😠',
    '평온': '😌',
    '불안': '😰',
    '만족': '😄',
    '피곤': '😴'
};

const weightChangeColors = {
    'weight-loss': '#4CAF50',
    'weight-gain': '#f44336',
    'weight-same': '#9E9E9E'
};