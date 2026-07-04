import { Link } from 'react-router-dom';

const riskColors = {
  LOW:    { color: '#1b5e20', bg: '#e8f5e9', border: '#81c784' },
  MEDIUM: { color: '#bf360c', bg: '#fff3e0', border: '#ffb74d' },
  HIGH:   { color: '#b71c1c', bg: '#ffebee', border: '#e57373' },
  AVOID:  { color: '#880e4f', bg: '#fce4ec', border: '#f06292' },
};

// Guess a smart title from ingredient list when productName is null
const getSmartTitle = (productName, ingredientList) => {
  if (productName && productName.trim()) return productName;
  if (!ingredientList) return 'Ingredient Analysis';
  // Use first 2-3 ingredients as the title
  const first = ingredientList.split(',').slice(0, 2).map(s => s.trim()).join(', ');
  return first.length > 40 ? first.substring(0, 40) + '…' : first;
};

export default function HistoryCard({ item, onDelete }) {
  // Backend sends 'analyzedAt', not 'createdAt'
  const dateStr = item.analyzedAt || item.createdAt;
  const date = dateStr
    ? new Date(dateStr).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' })
    : 'Unknown date';

  const risk = item.overallRisk?.toUpperCase() || 'UNKNOWN';
  const riskStyle = riskColors[risk] || { color: '#757575', bg: '#f5f5f5', border: '#bdbdbd' };
  const title = getSmartTitle(item.productName, item.ingredientList);

  return (
    <div style={styles.card}>
      <div style={styles.left}>
        <p style={styles.date}>{date}</p>
        <h4 style={styles.title}>{title}</h4>
        <p style={styles.ingredients} title={item.ingredientList}>
          {item.ingredientList?.length > 80
            ? item.ingredientList.substring(0, 80) + '…'
            : item.ingredientList}
        </p>
      </div>

      <div style={styles.right}>
        <div style={{ ...styles.riskBadge, backgroundColor: riskStyle.bg, color: riskStyle.color, borderColor: riskStyle.border }}>
          <span style={styles.riskScore}>{item.riskScore}/10</span>
          <span style={styles.riskLabel}>{risk}</span>
        </div>
        <div style={styles.actions}>
          <Link
            to={`/history/${item.id}`}
            state={{ analysisItem: item }}
            style={{ ...styles.btnBase, backgroundColor: '#e0e0e0', color: '#333' }}
          >
            Details
          </Link>
          <button
            style={{ ...styles.btnBase, backgroundColor: '#d32f2f', color: 'white', cursor: 'pointer' }}
            onClick={() => onDelete(item.id)}
          >
            Delete
          </button>
        </div>
      </div>
    </div>
  );
}

const styles = {
  card: {
    background: 'white',
    border: '1px solid #e0e0e0',
    borderRadius: '10px',
    boxShadow: '0 2px 8px rgba(0,0,0,0.06)',
    padding: '1.25rem 1.5rem',
    marginBottom: '1rem',
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
    gap: '1rem',
    flexWrap: 'wrap',
  },
  left: {
    flex: 1,
    minWidth: '180px',
  },
  date: {
    fontSize: '0.75rem',
    color: 'var(--text-muted)',
    marginBottom: '0.3rem',
  },
  title: {
    fontWeight: '600',
    fontSize: '1rem',
    marginBottom: '0.35rem',
    color: '#222',
  },
  ingredients: {
    fontSize: '0.78rem',
    color: 'var(--text-muted)',
    lineHeight: '1.4',
  },
  right: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'flex-end',
    gap: '0.75rem',
  },
  riskBadge: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    padding: '0.5rem 1.1rem',
    borderRadius: '8px',
    minWidth: '80px',
    border: '1px solid',
  },
  riskScore: {
    fontWeight: '700',
    fontSize: '1.1rem',
  },
  riskLabel: {
    fontSize: '0.65rem',
    fontWeight: '700',
    textTransform: 'uppercase',
    letterSpacing: '0.5px',
    marginTop: '2px',
  },
  actions: {
    display: 'flex',
    gap: '0.5rem',
  },
  btnBase: {
    padding: '0.4rem 1rem',
    borderRadius: '20px',
    fontSize: '0.82rem',
    fontWeight: '500',
    border: 'none',
    fontFamily: 'inherit',
    textDecoration: 'none',
    display: 'inline-flex',
    alignItems: 'center',
  },
};
