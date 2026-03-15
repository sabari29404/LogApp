import React, { useEffect, useState } from 'react';

const s = {
  header:   { position: 'sticky', top: 0, zIndex: 50, display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '14px 40px', background: 'rgba(7,9,13,.9)', backdropFilter: 'blur(12px)', borderBottom: '1px solid var(--border)' },
  brand:    { display: 'flex', alignItems: 'center', gap: 8 },
  brandHex: { fontSize: 20, color: 'var(--accent)' },
  brandName:{ fontFamily: 'var(--fh)', fontSize: 18, fontWeight: 800 },
  hRight:   { display: 'flex', alignItems: 'center', gap: 12 },
  backBtn:  { background: 'transparent', border: '1px solid var(--border)', borderRadius: 8, padding: '7px 16px', color: 'var(--muted)', fontFamily: 'var(--fm)', fontSize: 13, cursor: 'pointer' },
  logoutBtn:{ background: 'transparent', border: '1px solid var(--border)', borderRadius: 8, padding: '7px 16px', color: 'var(--muted)', fontFamily: 'var(--fm)', fontSize: 13, cursor: 'pointer' },
  main:     { maxWidth: 960, margin: '0 auto', padding: '60px 24px 80px', display: 'flex', flexDirection: 'column', gap: 24 },
  title:    { fontFamily: 'var(--fh)', fontSize: 28, fontWeight: 800 },
  titleSpan:{ color: 'var(--accent)' },
  sub:      { fontSize: 13, color: 'var(--muted)', marginTop: 6 },
  tableCard:{ background: 'var(--surface)', border: '1px solid var(--border)', borderRadius: 12, overflow: 'hidden' },
  table:    { width: '100%', borderCollapse: 'collapse' },
  thead:    { background: 'var(--surf2)' },
  th:       { padding: '12px 20px', textAlign: 'left', fontSize: 11, letterSpacing: '.08em', textTransform: 'uppercase', color: 'var(--muted)', fontWeight: 600, borderBottom: '1px solid var(--border)' },
  td:       { padding: '14px 20px', fontSize: 13, borderBottom: '1px solid var(--border)', color: 'var(--text)' },
  tdLast:   { padding: '14px 20px', fontSize: 13, color: 'var(--text)' },
  badgeYes: { background: 'rgba(63,185,80,.1)', color: 'var(--green)', border: '1px solid rgba(63,185,80,.25)', borderRadius: 6, padding: '2px 10px', fontSize: 11 },
  badgeNo:  { background: 'rgba(248,81,73,.1)', color: 'var(--red)',   border: '1px solid rgba(248,81,73,.25)', borderRadius: 6, padding: '2px 10px', fontSize: 11 },
  empty:    { padding: '60px 20px', textAlign: 'center', color: 'var(--muted)', fontSize: 14 },
  loading:  { padding: '40px 20px', textAlign: 'center', color: 'var(--muted)', fontSize: 14 },
};

function CompaniesPage() {
  const [companies, setCompanies] = useState([]);
  const [loading,   setLoading]   = useState(true);
  const [error,     setError]     = useState(null);

  useEffect(() => {
    fetch('http://localhost:8000/api/companies', { credentials: 'include' })
      .then(r => {
        if (r.status === 401) { window.location.href = '/'; return null; }
        return r.json();
      })
      .then(data => { if (data) setCompanies(data); setLoading(false); })
      .catch(() => { setError('Failed to load companies.'); setLoading(false); });
  }, []);

  const handleLogout = async () => {
    await fetch('http://localhost:8000/api/auth/logout', { method: 'POST', credentials: 'include' });
    window.location.href = '/';
  };

  return (
    <>
      <header style={s.header}>
        <div style={s.brand}>
          <span style={s.brandHex}>⬡</span>
          <span style={s.brandName}>LogApp</span>
        </div>
        <div style={s.hRight}>
          <button style={s.backBtn}   onClick={() => window.location.href = '/success'}>← Back</button>
          <button style={s.logoutBtn} onClick={handleLogout}>Sign Out</button>
        </div>
      </header>

      <main style={s.main}>
        <div>
          <h1 style={s.title}>🏢 <span style={s.titleSpan}>Companies</span></h1>
          <p style={s.sub}>Data loaded from PostgreSQL — only authenticated users can access this page.</p>
        </div>

        <div style={s.tableCard}>
          {loading && <div style={s.loading}>Loading...</div>}
          {error   && <div style={s.empty}>{error}</div>}

          {!loading && !error && companies.length === 0 && (
            <div style={s.empty}>
              <div style={{ fontSize: 36, marginBottom: 12 }}>📭</div>
              <div>No companies found in the database.</div>
            </div>
          )}

          {!loading && !error && companies.length > 0 && (
            <table style={s.table}>
              <thead style={s.thead}>
                <tr>
                  <th style={s.th}>ID</th>
                  <th style={s.th}>Company Name</th>
                  <th style={s.th}>Started At</th>
                  <th style={s.th}>Registered</th>
                </tr>
              </thead>
              <tbody>
                {companies.map((c, i) => {
                  const tdStyle = i === companies.length - 1 ? s.tdLast : s.td;
                  return (
                    <tr key={c.id}>
                      <td style={tdStyle}>{c.id}</td>
                      <td style={tdStyle}>{c.companyName}</td>
                      <td style={tdStyle}>{c.startedAt}</td>
                      <td style={tdStyle}>
                        {c.registered
                          ? <span style={s.badgeYes}>✓ Yes</span>
                          : <span style={s.badgeNo}>✗ No</span>}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          )}
        </div>
      </main>
    </>
  );
}

export default CompaniesPage;
