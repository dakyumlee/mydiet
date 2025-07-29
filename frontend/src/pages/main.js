async function fetchClaudeMessage(userId) {
  try {
    const res = await fetch(`/api/claude/message?userId=${userId}`);
    const text = await res.text();
    document.getElementById('claude-response').innerText = text;
  } catch (e) {
    document.getElementById('claude-response').innerText = 'Claude 응답 실패: ' + e.message;
  }
}

fetchClaudeMessage(1);
