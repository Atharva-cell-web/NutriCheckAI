import { useState, useRef } from 'react';

export default function UploadBox({ onUpload }) {
  const [file, setFile] = useState(null);
  const [preview, setPreview] = useState(null);
  const fileInputRef = useRef(null);

  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0];
    if (!selectedFile) return;
    setFile(selectedFile);
    setPreview(URL.createObjectURL(selectedFile));
  };

  const handleDrop = (e) => {
    e.preventDefault();
    const droppedFile = e.dataTransfer.files[0];
    if (droppedFile && (droppedFile.type === 'image/jpeg' || droppedFile.type === 'image/png')) {
      setFile(droppedFile);
      setPreview(URL.createObjectURL(droppedFile));
    }
  };

  const handleDragOver = (e) => e.preventDefault();

  return (
    <div>
      <input
        type="file"
        accept="image/png, image/jpeg"
        onChange={handleFileChange}
        style={{ display: 'none' }}
        ref={fileInputRef}
        id="image-upload-input"
      />

      {!preview ? (
        <div
          className="card text-center"
          onClick={() => fileInputRef.current.click()}
          onDrop={handleDrop}
          onDragOver={handleDragOver}
          style={styles.dropZone}
        >
          <div style={{ fontSize: '3rem', marginBottom: '0.75rem' }}>📷</div>
          <p style={{ fontWeight: '500', marginBottom: '0.5rem' }}>Click to select an image</p>
          <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
            or drag and drop here — JPEG or PNG only
          </p>
        </div>
      ) : (
        <div className="card text-center" style={{ padding: '1.5rem' }}>
          <img
            src={preview}
            alt="Selected label preview"
            style={styles.preview}
          />
          <div style={{ marginTop: '1.25rem', display: 'flex', justifyContent: 'center', gap: '1rem', flexWrap: 'wrap' }}>
            <button
              className="btn"
              style={{ backgroundColor: 'var(--neutral-color)' }}
              onClick={() => { setFile(null); setPreview(null); }}
            >
              Remove
            </button>
            <button
              className="btn"
              onClick={() => fileInputRef.current.click()}
              style={{ backgroundColor: 'var(--neutral-color)' }}
            >
              Change Image
            </button>
            <button className="btn" onClick={() => onUpload(file)}>
              Upload & Analyze
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

const styles = {
  dropZone: {
    border: '2px dashed #c8e6c9',
    backgroundColor: '#fafffe',
    cursor: 'pointer',
    padding: '3rem 2rem',
    transition: 'border-color 0.2s',
  },
  preview: {
    maxWidth: '100%',
    maxHeight: '320px',
    borderRadius: '8px',
    objectFit: 'contain',
    boxShadow: 'var(--shadow-soft)',
  },
};
