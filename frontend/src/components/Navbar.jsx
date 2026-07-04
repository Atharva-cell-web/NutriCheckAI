import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Navbar() {
  const { isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav style={styles.nav}>
      <div style={styles.inner}>
        <Link to={isAuthenticated ? '/dashboard' : '/'} style={styles.brand}>
          🥦 NutriCheckAI
        </Link>

        <div style={styles.links}>
          {isAuthenticated ? (
            <>
              <Link to="/dashboard" style={styles.link}>Dashboard</Link>
              <Link to="/manual-analysis" style={styles.link}>Analyze</Link>
              <Link to="/history" style={styles.link}>History</Link>
              <Link to="/profile" style={styles.link}>Profile</Link>
              <button onClick={handleLogout} style={styles.logoutBtn}>Logout</button>
            </>
          ) : (
            <>
              <Link to="/login" style={styles.link}>Login</Link>
              <Link to="/register" style={styles.registerBtn}>Register</Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
}

const styles = {
  nav: {
    backgroundColor: '#1b5e20',
    padding: '0 1.5rem',
    position: 'sticky',
    top: 0,
    zIndex: 100,
    boxShadow: '0 2px 8px rgba(0,0,0,0.2)',
  },
  inner: {
    maxWidth: '1100px',
    margin: '0 auto',
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    height: '62px',
  },
  brand: {
    color: 'white',
    textDecoration: 'none',
    fontSize: '1.25rem',
    fontWeight: '700',
    letterSpacing: '-0.5px',
  },
  links: {
    display: 'flex',
    alignItems: 'center',
    gap: '0.25rem',
  },
  link: {
    color: 'rgba(255,255,255,0.85)',
    textDecoration: 'none',
    padding: '0.4rem 0.8rem',
    borderRadius: '6px',
    fontSize: '0.9rem',
    transition: 'background 0.2s',
  },
  registerBtn: {
    color: '#1b5e20',
    textDecoration: 'none',
    padding: '0.4rem 1rem',
    borderRadius: '20px',
    fontSize: '0.9rem',
    fontWeight: '600',
    backgroundColor: 'white',
    marginLeft: '0.5rem',
  },
  logoutBtn: {
    color: 'white',
    border: '1px solid rgba(255,255,255,0.4)',
    background: 'transparent',
    padding: '0.4rem 1rem',
    borderRadius: '20px',
    fontSize: '0.9rem',
    cursor: 'pointer',
    marginLeft: '0.5rem',
    fontFamily: 'inherit',
    transition: 'background 0.2s',
  },
};
