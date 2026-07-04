export default function Loader({ message = "Loading..." }) {
  return (
    <div style={styles.container}>
      <div style={styles.spinner}></div>
      <p style={styles.text}>{message}</p>
    </div>
  );
}

const styles = {
  container: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    padding: '3rem 2rem',
    gap: '1rem',
  },
  spinner: {
    border: '3px solid #e0e0e0',
    width: '40px',
    height: '40px',
    borderRadius: '50%',
    borderLeftColor: 'var(--primary-color)',
    animation: 'spin 0.9s linear infinite',
  },
  text: {
    color: 'var(--text-muted)',
    fontSize: '0.9rem',
    textAlign: 'center',
    maxWidth: '300px',
  },
};
