import { useState, useEffect } from 'react';
import { getHistory, deleteHistory } from '../services/historyService';
import HistoryCard from '../components/HistoryCard';
import Loader from '../components/Loader';
import { Link } from 'react-router-dom';

export default function History() {
  const [historyList, setHistoryList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchHistory();
  }, []);

  const fetchHistory = async () => {
    try {
      setLoading(true);
      const data = await getHistory();
      setHistoryList(data || []);
    } catch (err) {
      setError('Failed to load history. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this analysis report?')) return;
    try {
      await deleteHistory(id);
      setHistoryList((prev) => prev.filter((item) => item.id !== id));
    } catch (err) {
      alert('Failed to delete. Please try again.');
    }
  };

  if (loading) return <Loader message="Loading your analysis history..." />;

  return (
    <div className="container" style={{ maxWidth: '800px' }}>
      <div className="page-header">
        <h2>Analysis History</h2>
        <p>All your past ingredient analyses, sorted by most recent.</p>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      {historyList.length === 0 && !error ? (
        <div className="card text-center" style={{ padding: '3.5rem 2rem' }}>
          <p style={{ fontSize: '2.5rem', marginBottom: '1rem' }}>📋</p>
          <p style={{ fontWeight: '500', marginBottom: '0.5rem' }}>No history yet</p>
          <p style={{ color: 'var(--text-muted)', marginBottom: '1.5rem', fontSize: '0.9rem' }}>
            Start by analyzing your first product.
          </p>
          <Link to="/manual-analysis" className="btn" style={{ textDecoration: 'none' }}>
            Analyze Now
          </Link>
        </div>
      ) : (
        historyList.map((item) => (
          <HistoryCard key={item.id} item={item} onDelete={handleDelete} />
        ))
      )}
    </div>
  );
}
