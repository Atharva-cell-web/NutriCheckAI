import { useLocation, Link, Navigate } from 'react-router-dom';
import AnalysisCard from '../components/AnalysisCard';

export default function AnalysisDetails() {
  const location = useLocation();
  const analysisItem = location.state?.analysisItem;

  if (!analysisItem) {
    return <Navigate to="/history" replace />;
  }

  const date = analysisItem.createdAt
    ? new Date(analysisItem.createdAt).toLocaleDateString('en-IN', {
        day: 'numeric',
        month: 'long',
        year: 'numeric',
      })
    : 'Unknown date';

  // Map the stored history object into the AnalysisCard shape
  const formattedAnalysis = {
    riskScore: analysisItem.riskScore,
    overallRisk: analysisItem.overallRisk,
    summary: analysisItem.aiResponse,
    recommendation: 'Refer to the AI summary above for specific guidance based on this product.',
    flaggedIngredients: [],
  };

  return (
    <div className="container" style={{ maxWidth: '800px' }}>
      <div style={{ marginBottom: '1.5rem' }}>
        <Link to="/history" style={{ color: 'var(--primary-color)', textDecoration: 'none', fontSize: '0.9rem' }}>
          ← Back to History
        </Link>
      </div>

      <div className="card" style={{ marginBottom: '0' }}>
        <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginBottom: '0.5rem' }}>{date}</p>
        <h2 style={{ fontWeight: '700', marginBottom: '0.75rem', color: 'var(--text-color)' }}>
          {analysisItem.productName || 'Analysis Report'}
        </h2>
        <div style={{ padding: '0.75rem', background: '#f9f9f9', borderRadius: '6px' }}>
          <p style={{ fontSize: '0.85rem', color: '#555' }}>
            <strong>Ingredients:</strong>{' '}
            {analysisItem.ingredientList || 'Not available'}
          </p>
        </div>
      </div>

      <AnalysisCard analysis={formattedAnalysis} />
    </div>
  );
}
