const getRiskConfig = (risk) => {
  switch (risk?.toUpperCase()) {
    case 'LOW':    return { color: '#1b5e20', bg: 'linear-gradient(135deg,#e8f5e9,#c8e6c9)', border: '#81c784', emoji: '✅' };
    case 'MEDIUM': return { color: '#bf360c', bg: 'linear-gradient(135deg,#fff3e0,#ffe0b2)', border: '#ffb74d', emoji: '⚠️' };
    case 'HIGH':   return { color: '#b71c1c', bg: 'linear-gradient(135deg,#ffebee,#ffcdd2)', border: '#e57373', emoji: '🚨' };
    case 'AVOID':  return { color: '#880e4f', bg: 'linear-gradient(135deg,#fce4ec,#f8bbd0)', border: '#f06292', emoji: '🚫' };
    default:       return { color: '#424242', bg: 'linear-gradient(135deg,#f5f5f5,#eeeeee)', border: '#bdbdbd', emoji: '❓' };
  }
};

const getScoreColor = (score) => {
  if (score >= 70) return '#b71c1c';
  if (score >= 40) return '#e65100';
  return '#2e7d32';
};

export default function AnalysisCard({ analysis }) {
  if (!analysis) return null;

  const riskLevel = analysis.overallRisk?.toUpperCase() || 'UNKNOWN';
  const riskCfg = getRiskConfig(riskLevel);
  const score = analysis.riskScore ?? 0;

  return (
    <div style={styles.wrapper}>
      {/* Header gradient banner */}
      <div style={{ ...styles.header, background: riskCfg.bg, borderColor: riskCfg.border }}>
        <div style={styles.headerContent}>
          <div>
            <p style={{ fontSize: '0.75rem', fontWeight: '600', textTransform: 'uppercase', letterSpacing: '1px', color: riskCfg.color, opacity: 0.7 }}>Analysis Complete</p>
            <h2 style={{ fontSize: '1.5rem', fontWeight: '800', color: riskCfg.color, marginTop: '0.2rem' }}>
              {riskCfg.emoji} {riskLevel}
            </h2>
          </div>
          <div style={{ textAlign: 'center' }}>
            <div style={{ ...styles.scoreDial, borderColor: getScoreColor(score) }}>
              <span style={{ fontSize: '1.8rem', fontWeight: '800', color: getScoreColor(score) }}>{score}</span>
              <span style={{ fontSize: '0.65rem', color: '#777', display: 'block', marginTop: '-2px' }}>OUT OF 10</span>
            </div>
            <p style={{ fontSize: '0.7rem', color: riskCfg.color, fontWeight: '600', marginTop: '0.4rem' }}>RISK SCORE</p>
          </div>
        </div>
      </div>

      <div style={styles.body}>
        {/* Flagged Ingredients */}
        {analysis.flaggedIngredients && analysis.flaggedIngredients.length > 0 && (
          <div style={styles.section}>
            <h3 style={styles.sectionTitle}>🚩 Flagged Ingredients ({analysis.flaggedIngredients.length})</h3>
            <div style={styles.flagGrid}>
              {analysis.flaggedIngredients.map((item, index) => (
                <div key={index} style={styles.flagCard}>
                  <div style={styles.flagName}>
                    🔴 {item.ingredient || item.ingredientName || 'Unknown Ingredient'}
                  </div>
                  <p style={styles.flagReason}>{item.reason}</p>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Summary */}
        {analysis.summary && (
          <div style={styles.section}>
            <h3 style={styles.sectionTitle}>📋 Summary</h3>
            <p style={styles.paragraph}>{analysis.summary}</p>
          </div>
        )}

        {/* Recommendation */}
        {analysis.recommendation && (
          <div style={{ ...styles.section, marginBottom: 0 }}>
            <h3 style={styles.sectionTitle}>💡 Recommendation</h3>
            <div style={styles.recBox}>
              <p style={styles.paragraph}>{analysis.recommendation}</p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

const styles = {
  wrapper: {
    borderRadius: '14px',
    overflow: 'hidden',
    boxShadow: '0 4px 20px rgba(0,0,0,0.1)',
    marginTop: '1.5rem',
    border: '1px solid #e0e0e0',
    background: '#fff',
  },
  header: {
    padding: '1.5rem 1.75rem',
    borderBottom: '2px solid',
  },
  headerContent: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  scoreDial: {
    width: '80px',
    height: '80px',
    borderRadius: '50%',
    border: '4px solid',
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    background: 'white',
    boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
  },
  body: {
    padding: '1.5rem 1.75rem',
  },
  section: {
    marginBottom: '1.75rem',
  },
  sectionTitle: {
    fontSize: '0.9rem',
    fontWeight: '700',
    textTransform: 'uppercase',
    letterSpacing: '0.5px',
    color: '#555',
    marginBottom: '0.75rem',
    paddingBottom: '0.4rem',
    borderBottom: '1px solid #f0f0f0',
  },
  flagGrid: {
    display: 'flex',
    flexDirection: 'column',
    gap: '0.6rem',
  },
  flagCard: {
    background: '#fff5f5',
    border: '1px solid #ffcdd2',
    borderLeft: '4px solid #e53935',
    borderRadius: '8px',
    padding: '0.75rem 1rem',
  },
  flagName: {
    fontWeight: '700',
    fontSize: '0.9rem',
    color: '#c62828',
    marginBottom: '0.3rem',
  },
  flagReason: {
    fontSize: '0.85rem',
    color: '#555',
    lineHeight: '1.5',
    margin: 0,
  },
  paragraph: {
    color: '#444',
    fontSize: '0.9rem',
    lineHeight: '1.7',
    margin: 0,
  },
  recBox: {
    background: 'linear-gradient(135deg, #e8f5e9, #f1f8e9)',
    border: '1px solid #a5d6a7',
    borderLeft: '4px solid #2e7d32',
    borderRadius: '8px',
    padding: '1rem 1.25rem',
  },
};
