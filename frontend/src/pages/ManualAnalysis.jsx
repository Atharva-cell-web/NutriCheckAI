import { useState } from 'react';
import { analyzeManual } from '../services/aiService';
import Loader from '../components/Loader';
import AnalysisCard from '../components/AnalysisCard';

export default function ManualAnalysis() {
  const [ingredientsText, setIngredientsText] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [result, setResult] = useState(null);

  const handleAnalyze = async () => {
    if (!ingredientsText.trim()) {
      setError('Please enter some ingredients before analyzing.');
      return;
    }

    const ingredientsList = ingredientsText
      .split(',')
      .map((item) => item.trim())
      .filter((i) => i.length > 0);

    if (ingredientsList.length === 0) {
      setError('Please provide at least one ingredient.');
      return;
    }

    try {
      setLoading(true);
      setError(null);
      setResult(null);
      const response = await analyzeManual(ingredientsList);
      setResult(response);
    } catch (err) {
      setError('Analysis failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleReset = () => {
    setResult(null);
    setIngredientsText('');
    setError(null);
  };

  return (
    <div className="container" style={{ maxWidth: '800px' }}>
      <div className="page-header">
        <h2>Manual Analysis</h2>
        <p>Enter the ingredients list separated by commas to get an AI-powered safety analysis.</p>
      </div>

      {!result ? (
        <div className="card">
          {error && <div className="alert alert-error">{error}</div>}

          <div className="form-group">
            <label htmlFor="ingredients-input">Ingredients List</label>
            <textarea
              id="ingredients-input"
              className="form-control"
              rows="6"
              placeholder="e.g. Sugar, High Fructose Corn Syrup, Citric Acid, Natural Flavors, Sodium Benzoate..."
              value={ingredientsText}
              onChange={(e) => setIngredientsText(e.target.value)}
              disabled={loading}
              style={{ fontFamily: 'inherit' }}
            ></textarea>
            <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginTop: '0.4rem' }}>
              Tip: Copy the ingredients directly from the product label.
            </p>
          </div>

          <button
            className="btn"
            onClick={handleAnalyze}
            disabled={loading}
            style={{ width: '100%', padding: '0.75rem', fontSize: '1rem' }}
          >
            {loading ? 'Analyzing...' : '🔍 Analyze Ingredients'}
          </button>
        </div>
      ) : (
        <div style={{ marginBottom: '1rem' }}>
          <button className="btn" style={{ backgroundColor: 'var(--neutral-color)' }} onClick={handleReset}>
            ← Analyze New Ingredients
          </button>
        </div>
      )}

      {loading && (
        <Loader message="Our AI is reviewing the ingredients. This may take a few seconds..." />
      )}

      {result && !loading && <AnalysisCard analysis={result} />}
    </div>
  );
}
