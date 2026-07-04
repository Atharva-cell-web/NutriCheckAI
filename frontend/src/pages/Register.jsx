import { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { register } from '../services/authService';

export default function Register() {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  // Clear any stale/old token when the user lands on Register page
  useEffect(() => {
    localStorage.removeItem('token');
  }, []);

  // Auto-redirect to login 3 seconds after successful registration
  useEffect(() => {
    if (success) {
      const timer = setTimeout(() => navigate('/login'), 3000);
      return () => clearTimeout(timer);
    }
  }, [success, navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (password.length < 6) {
      setError('Password must be at least 6 characters.');
      return;
    }
    setLoading(true);
    setError(null);
    try {
      await register({ name, email, password });
      setSuccess(true); // Show success message, auto-redirect kicks in
    } catch (err) {
      const serverMsg = err.response?.data?.message || err.response?.data;
      if (typeof serverMsg === 'string') {
        setError(serverMsg);
      } else if (err.message === 'Network Error') {
        setError('Cannot connect to server. Make sure the backend is running on port 8080.');
      } else {
        setError('Registration failed. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  // Show success screen after registration
  if (success) {
    return (
      <div style={styles.wrapper}>
        <div className="card text-center" style={styles.card}>
          <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>✅</div>
          <h2 style={{ color: 'var(--primary-color)', marginBottom: '0.75rem' }}>Account Created!</h2>
          <p style={{ color: '#555', marginBottom: '0.5rem' }}>
            Welcome, <strong>{name}</strong>!
          </p>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', marginBottom: '1.5rem' }}>
            Redirecting you to login in a moment...
          </p>
          <Link to="/login" className="btn" style={{ textDecoration: 'none' }}>
            Go to Login Now
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div style={styles.wrapper}>
      <div className="card" style={styles.card}>
        <div className="text-center" style={{ marginBottom: '1.75rem' }}>
          <h1 style={styles.logo}>🥦 NutriCheckAI</h1>
          <p style={styles.subtitle}>Your personal food safety assistant.</p>
        </div>

        <h2 style={styles.heading}>Create Account</h2>

        {error && <div className="alert alert-error">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="reg-name">Full Name</label>
            <input
              id="reg-name"
              type="text"
              className="form-control"
              placeholder="John Doe"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
              disabled={loading}
            />
          </div>
          <div className="form-group">
            <label htmlFor="reg-email">Email</label>
            <input
              id="reg-email"
              type="email"
              className="form-control"
              placeholder="you@example.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              disabled={loading}
            />
          </div>
          <div className="form-group">
            <label htmlFor="reg-password">Password</label>
            <input
              id="reg-password"
              type="password"
              className="form-control"
              placeholder="Min. 6 characters"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              disabled={loading}
            />
          </div>
          <button
            type="submit"
            className="btn"
            style={{ width: '100%', marginTop: '0.5rem', padding: '0.75rem' }}
            disabled={loading}
          >
            {loading ? 'Creating account...' : 'Create Account'}
          </button>
        </form>

        <p className="text-center mt-2" style={{ color: '#666', fontSize: '0.9rem' }}>
          Already have an account?{' '}
          <Link to="/login" style={{ color: 'var(--primary-color)', fontWeight: '600' }}>
            Sign in
          </Link>
        </p>
      </div>
    </div>
  );
}

const styles = {
  wrapper: {
    minHeight: 'calc(100vh - 62px)',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    padding: '2rem 1rem',
    backgroundColor: 'var(--bg-color)',
  },
  card: {
    width: '100%',
    maxWidth: '420px',
  },
  logo: {
    fontSize: '1.75rem',
    fontWeight: '800',
    color: 'var(--primary-color)',
  },
  subtitle: {
    color: 'var(--text-muted)',
    fontSize: '0.9rem',
    marginTop: '0.25rem',
  },
  heading: {
    fontSize: '1.25rem',
    fontWeight: '600',
    marginBottom: '1.25rem',
  },
};
