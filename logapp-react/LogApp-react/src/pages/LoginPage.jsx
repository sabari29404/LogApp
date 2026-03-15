import React, { useState } from 'react';

const s = {
  body: {
    background: '#f0f2f5', minHeight: '100vh',
    display: 'flex', justifyContent: 'center', alignItems: 'center',
    fontFamily: 'Arial, sans-serif',
  },
  card: {
    background: 'white', borderRadius: 8,
    padding: 32, width: 380,
    boxShadow: '0 2px 16px rgba(0,0,0,0.12)',
  },
  h2:   { textAlign: 'center', marginBottom: 24, color: '#333' },
  tabs: { display: 'flex', marginBottom: 24, borderBottom: '2px solid #eee' },
  tab: (active) => ({
    flex: 1, padding: 10, textAlign: 'center', cursor: 'pointer',
    color: active ? '#4285f4' : '#888', fontWeight: 'bold',
    border: 'none', background: 'none', fontSize: 14,
    borderBottom: active ? '2px solid #4285f4' : 'none',
    marginBottom: active ? -2 : 0,
  }),
  input: {
    width: '100%', padding: '10px 12px', marginBottom: 14,
    border: '1px solid #ddd', borderRadius: 4, fontSize: 14,
    boxSizing: 'border-box',
  },
  btn: {
    width: '100%', padding: 10, background: '#4285f4',
    color: 'white', border: 'none', borderRadius: 4,
    fontSize: 15, cursor: 'pointer',
  },
  oauthBtn: {
    width: '100%', padding: 10, marginBottom: 10,
    border: '1px solid #ddd', borderRadius: 4, fontSize: 14,
    cursor: 'pointer', background: 'white',
    display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 8,
  },
  error:   { color: 'red',   fontSize: 13, marginBottom: 12, textAlign: 'center' },
  success: { color: 'green', fontSize: 13, marginBottom: 12, textAlign: 'center' },
};

function LoginPage() {
  const [tab,     setTab]     = useState('form');
  const [user,    setUser]    = useState('');
  const [pass,    setPass]    = useState('');
  const [error,   setError]   = useState('');
  const [loading, setLoading] = useState(false);

  const params   = new URLSearchParams(window.location.search);
  const urlError = params.get('error');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      // POST credentials as form data to Spring Boot /login
      const formData = new FormData();
      formData.append('username', user);
      formData.append('password', pass);

      const res = await fetch('http://localhost:8000/login', {
        method: 'POST',
        body: formData,
        credentials: 'include',
        redirect: 'follow',
      });

      // Spring redirects to localhost:3000/success on success
      // or to localhost:3000/?error=true on failure
      if (res.redirected) {
        window.location.href = res.url;
      } else if (res.ok) {
        window.location.href = '/success';
      } else {
        setError('Invalid username or password.');
      }
    } catch {
      // Spring redirect goes to localhost:3000/success — browser follows it
      window.location.href = '/success';
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={s.body}>
      <div style={s.card}>
        <h2 style={s.h2}>Welcome</h2>

        <div style={s.tabs}>
          <button style={s.tab(tab === 'form')}  onClick={() => setTab('form')}>Username</button>
          <button style={s.tab(tab === 'oauth')} onClick={() => setTab('oauth')}>Social Login</button>
        </div>

        {tab === 'form' && (
          <div>
            {(error || urlError) && (
              <div style={s.error}>{error || 'Invalid username or password.'}</div>
            )}
            <form onSubmit={handleSubmit}>
              <input
                style={s.input} type="text" placeholder="Username"
                value={user} onChange={e => setUser(e.target.value)}
                required autoFocus
              />
              <input
                style={s.input} type="password" placeholder="Password"
                value={pass} onChange={e => setPass(e.target.value)}
                required
              />
              <button type="submit" style={s.btn} disabled={loading}>
                {loading ? 'Signing in...' : 'Sign In'}
              </button>
            </form>
          </div>
        )}

        {tab === 'oauth' && (
          <div>
            {urlError === 'oauth_failed' && (
              <div style={s.error}>OAuth2 login failed. Please try again.</div>
            )}
            {/* OAuth links go directly to Spring Boot via API Gateway */}
            <a href="http://localhost:8000/oauth2/authorization/google">
              <button style={s.oauthBtn}>🔵 Continue with Google</button>
            </a>
            <a href="http://localhost:8000/oauth2/authorization/github">
              <button style={s.oauthBtn}>⚫ Continue with GitHub</button>
            </a>
          </div>
        )}
      </div>
    </div>
  );
}

export default LoginPage;
