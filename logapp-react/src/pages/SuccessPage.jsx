import React, { useEffect, useState } from 'react';

const s = {
  header: {
    position: 'sticky', top: 0, zIndex: 50,
    display: 'flex', alignItems: 'center', justifyContent: 'space-between',
    padding: '14px 40px', background: 'rgba(7,9,13,.9)',
    backdropFilter: 'blur(12px)', borderBottom: '1px solid var(--border)',
  },
  brand:    { display: 'flex', alignItems: 'center', gap: 8 },
  brandHex: { fontSize: 20, color: 'var(--accent)' },
  brandName:{ fontFamily: 'var(--fh)', fontSize: 18, fontWeight: 800 },
  hRight:   { display: 'flex', alignItems: 'center', gap: 12 },
  authBadge:{ fontSize: 12, color: 'var(--muted)', background: 'var(--surf2)', border: '1px solid var(--border)', borderRadius: 100, padding: '5px 12px' },
  roleBadge:{ fontSize: 11, fontWeight: 600, background: 'rgba(63,185,80,.1)', color: 'var(--green)', border: '1px solid rgba(63,185,80,.25)', borderRadius: 100, padding: '4px 10px' },
  logoutBtn:{ background: 'transparent', border: '1px solid var(--border)', borderRadius: 8, padding: '7px 16px', color: 'var(--muted)', fontFamily: 'var(--fm)', fontSize: 13, cursor: 'pointer' },
  main:     { maxWidth: 760, margin: '0 auto', padding: '60px 24px 80px', display: 'flex', flexDirection: 'column', gap: 24 },
  banner:   { background: 'rgba(63,185,80,.07)', border: '1px solid rgba(63,185,80,.25)', borderRadius: 12, padding: '16px 22px', display: 'flex', alignItems: 'center', gap: 12, fontSize: 14, color: 'var(--green)' },
  greeting: { background: 'var(--surface)', border: '1px solid var(--border)', borderRadius: 16, padding: '40px 44px', position: 'relative', overflow: 'hidden' },
  avatar:   { width: 60, height: 60, background: 'linear-gradient(135deg, var(--accent), #d2a8ff)', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', fontFamily: 'var(--fh)', fontSize: 24, fontWeight: 800, color: '#000', marginBottom: 20 },
  h1:       { fontFamily: 'var(--fh)', fontSize: 'clamp(28px, 4vw, 42px)', fontWeight: 800, letterSpacing: '-.02em', marginBottom: 8 },
  accent:   { color: 'var(--accent)' },
  p:        { fontSize: 15, color: 'var(--muted)', lineHeight: 1.7, marginBottom: 28 },
  chips:    { display: 'flex', flexWrap: 'wrap', gap: 10 },
  chip:     { background: 'var(--surf2)', border: '1px solid var(--border)', borderRadius: 8, padding: '9px 16px', display: 'flex', flexDirection: 'column', gap: 3 },
  chipLbl:  { fontSize: 10, letterSpacing: '.1em', textTransform: 'uppercase', color: 'var(--muted)' },
  chipVal:  { fontSize: 13, color: 'var(--text)' },
  companiesBtn: { width: '100%', padding: 14, fontSize: 15, fontFamily: 'var(--fm)', background: 'var(--accent)', color: '#000', fontWeight: 700, border: 'none', borderRadius: 10, cursor: 'pointer' },
  infoCard: { background: 'var(--surface)', border: '1px solid var(--border)', borderRadius: 12, padding: '22px 26px' },
  infoH2:   { fontFamily: 'var(--fh)', fontSize: 14, fontWeight: 700, color: 'var(--muted)', textTransform: 'uppercase', letterSpacing: '.08em', marginBottom: 14 },
  infoRow:  { display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '9px 0', borderBottom: '1px solid var(--border)', fontSize: 13 },
  infoKey:  { color: 'var(--muted)' },
  infoVal:  { color: 'var(--text)' },
  badge:    { display: 'inline-block', background: 'rgba(88,166,255,.1)', color: 'var(--accent)', border: '1px solid rgba(88,166,255,.2)', borderRadius: 6, padding: '3px 10px', fontSize: 11 },
};

function SuccessPage() {
  const [user, setUser] = useState(null);

  useEffect(() => {
    // Fetch user info from Success-Service via API Gateway
    fetch('http://localhost:8000/api/user', { credentials: 'include' })
      .then(r => {
        if (r.status === 401) { window.location.href = '/'; return null; }
        return r.json();
      })
      .then(data => { if (data) setUser(data); })
      .catch(() => { window.location.href = '/'; });
  }, []);

  const handleLogout = async () => {
    await fetch('http://localhost:8000/api/auth/logout', {
      method: 'POST', credentials: 'include',
    });
    window.location.href = '/';
  };

  if (!user) return <div style={{ color: 'var(--muted)', padding: 40, textAlign: 'center' }}>Loading...</div>;

  const initial = user.name ? user.name[0].toUpperCase() : 'U';

  return (
    <>
      <header style={s.header}>
        <div style={s.brand}>
          <span style={s.brandHex}>⬡</span>
          <span style={s.brandName}>LogApp</span>
        </div>
        <div style={s.hRight}>
          <span style={s.authBadge}>{user.provider} Auth</span>
          <span style={s.roleBadge}>ROLE_USER</span>
          <button style={s.logoutBtn} onClick={handleLogout}>Sign Out</button>
        </div>
      </header>

      <main style={s.main}>
        <div style={s.banner}>
          <span style={{ fontSize: 20 }}>✅</span>
          <span>Authentication successful! You are now on the <strong>Success-Service</strong> microservice.</span>
        </div>

        <div style={s.greeting}>
          <div style={s.avatar}>{initial}</div>
          <h1 style={s.h1}>Welcome, <span style={s.accent}>{user.name}</span> 👋</h1>
          <p style={s.p}>
            You have successfully authenticated. Your JWT token was validated and your identity confirmed.
            Only authenticated users can access this page.
          </p>
          <div style={s.chips}>
            {[
              ['Logged in as',    user.name],
              ['Email / Username',user.email],
              ['Auth method',     user.provider],
              ['Role',            user.role],
            ].map(([lbl, val]) => (
              <div key={lbl} style={s.chip}>
                <span style={s.chipLbl}>{lbl}</span>
                <span style={s.chipVal}>{val}</span>
              </div>
            ))}
          </div>
        </div>

        <button style={s.companiesBtn} onClick={() => window.location.href = '/companies'}>
          🏢 View Companies
        </button>

        <div style={s.infoCard}>
          <h2 style={s.infoH2}>Session Details</h2>
          {[
            ['Username / Subject', user.username],
            ['Display Name',       user.name],
            ['Auth Provider',      user.provider],
            ['Granted Role',       user.role],
          ].map(([key, val], i, arr) => (
            <div key={key} style={{ ...s.infoRow, borderBottom: i === arr.length - 1 ? 'none' : '1px solid var(--border)' }}>
              <span style={s.infoKey}>{key}</span>
              <span style={s.infoVal}>{val}</span>
            </div>
          ))}
        </div>
      </main>
    </>
  );
}

export default SuccessPage;
