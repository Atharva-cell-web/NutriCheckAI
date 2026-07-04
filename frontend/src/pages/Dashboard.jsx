import { Link } from 'react-router-dom';

const dashboardCards = [
  {
    icon: '🔍',
    title: 'Manual Analysis',
    description: 'Type or paste a list of ingredients to get an instant AI safety analysis.',
    link: '/manual-analysis',
    label: 'Start Analyzing',
  },
  {
    icon: '📷',
    title: 'Image Analysis',
    description: 'Upload a photo of a product label and let our AI read and analyze it.',
    link: '/image-analysis',
    label: 'Upload Image',
  },
  {
    icon: '📋',
    title: 'Analysis History',
    description: 'View and manage all your past ingredient analyses in one place.',
    link: '/history',
    label: 'View History',
  },
];

export default function Dashboard() {
  return (
    <div className="container">
      <div className="page-header">
        <h2>Dashboard</h2>
        <p>What would you like to do today?</p>
      </div>

      <div style={styles.grid}>
        {dashboardCards.map((card) => (
          <div key={card.title} className="card" style={styles.card}>
            <div style={styles.icon}>{card.icon}</div>
            <h3 style={styles.title}>{card.title}</h3>
            <p style={styles.desc}>{card.description}</p>
            <Link to={card.link} className="btn" style={{ marginTop: 'auto', textDecoration: 'none', alignSelf: 'flex-start' }}>
              {card.label}
            </Link>
          </div>
        ))}
      </div>
    </div>
  );
}

const styles = {
  grid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))',
    gap: '1.5rem',
  },
  card: {
    display: 'flex',
    flexDirection: 'column',
    gap: '0.75rem',
    padding: '2rem',
  },
  icon: {
    fontSize: '2.25rem',
  },
  title: {
    fontSize: '1.1rem',
    fontWeight: '600',
  },
  desc: {
    color: 'var(--text-muted)',
    fontSize: '0.9rem',
    lineHeight: '1.5',
    flexGrow: 1,
  },
};
