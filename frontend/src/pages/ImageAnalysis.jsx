import { useState } from 'react';
import { analyzeImage } from '../services/aiService';
import Loader from '../components/Loader';
import AnalysisCard from '../components/AnalysisCard';
import UploadBox from '../components/UploadBox';

export default function ImageAnalysis() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [result, setResult] = useState(null);

  const handleUpload = async (file) => {
    try {
      setLoading(true);
      setError(null);
      setResult(null);
      const response = await analyzeImage(file);
      setResult(response);
    } catch (err) {
      const msg = err.response?.data?.message;
      setError(msg || 'Analysis failed. Please ensure the image is clear and try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleReset = () => {
    setResult(null);
    setError(null);
  };

  return (
    <div className="container" style={{ maxWidth: '800px' }}>
      <div className="page-header">
        <h2>Image Analysis</h2>
        <p>Upload a photo of the ingredient label. Our AI will read and analyze the ingredients for you.</p>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      {!loading && !result && <UploadBox onUpload={handleUpload} />}

      {loading && (
        <Loader message="Reading the label and analyzing ingredients. This may take up to 30 seconds..." />
      )}

      {result && !loading && (
        <>
          <button
            className="btn"
            style={{ backgroundColor: 'var(--neutral-color)', marginBottom: '1rem' }}
            onClick={handleReset}
          >
            ← Analyze Another Image
          </button>
          <AnalysisCard analysis={result} />
        </>
      )}
    </div>
  );
}
