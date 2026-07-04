import { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { login as loginService } from '../services/authService';
import { getProfile } from '../services/profileService';

export default function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const { login } = useAuth();

  // Clear any stale/invalid token when landing on Login page
  useEffect(() => {
    localStorage.removeItem('token');
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      // Step 1: Login and store token
      const response = await loginService({ email, password });
      login(response.token);

      // Step 2: Check if the user has already set up a profile
      try {
        await getProfile(); // If this succeeds, profile exists
        navigate('/dashboard'); // Returning user → go to dashboard
      } catch (profileErr) {
        // Profile doesn't exist (404) → new user, send to profile setup
        navigate('/profile', { state: { setup: true } });
      }

    } catch (err) {
      const serverMsg = err.response?.data?.message || err.response?.data;
      if (typeof serverMsg === 'string') {
        setError(serverMsg);
      } else if (err.message === 'Network Error') {
        setError('Cannot connect to server. Make sure the backend is running on port 8080.');
      } else {
        setError('Invalid email or password. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={styles.wrapper}>
      <div className="card" style={styles.card}>
        <div className="text-center" style={{ marginBottom: '1.75rem' }}>
          <h1 style={styles.logo}>🥦 NutriCheckAI</h1>
          <p style={styles.subtitle}>Check what's really in your food.</p>
        </div>

        <h2 style={styles.heading}>Sign In</h2>

        {error && <div className="alert alert-error">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="login-email">Email</label>
            <input
              id="login-email"
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
            <label htmlFor="login-password">Password</label>
            <input
              id="login-password"
              type="password"
              className="form-control"
              placeholder="••••••••"
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
            {loading ? 'Signing in...' : 'Sign In'}
          </button>
        </form>

        <p className="text-center mt-2" style={{ color: '#666', fontSize: '0.9rem' }}>
          Don't have an account?{' '}
          <Link to="/register" style={{ color: 'var(--primary-color)', fontWeight: '600' }}>
            Create one
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
