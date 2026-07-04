import { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { getProfile, createProfile, updateProfile } from '../services/profileService';

export default function Profile() {
  const navigate = useNavigate();
  const location = useLocation();

  // If redirected here after login as a new user, show setup mode
  const isSetup = location.state?.setup === true;

  const [isNewUser, setIsNewUser] = useState(isSetup);
  const [isEditing, setIsEditing] = useState(isSetup);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);

  const emptyForm = {
    age: '',
    gender: '',
    skinType: '',
    dietPreference: '',
    allergies: '',
    medicalConditions: '',
    pregnant: false,
  };
  const [form, setForm] = useState(emptyForm);

  // Existing profile data for display (name, email come from user object)
  const [profileMeta, setProfileMeta] = useState({ name: '', email: '' });

  useEffect(() => {
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    try {
      setLoading(true);
      const data = await getProfile();
      // Profile exists — populate form
      setForm({
        age: data.age ?? '',
        gender: data.gender ?? '',
        skinType: data.skinType ?? '',
        dietPreference: data.dietPreference ?? '',
        allergies: data.allergies ?? '',
        medicalConditions: data.medicalConditions ?? '',
        pregnant: data.pregnant ?? false,
      });
      if (data.user) {
        setProfileMeta({ name: data.user.name, email: data.user.email });
      }
      setIsNewUser(false);
      setIsEditing(isSetup); // keep editing open if came from setup flow
    } catch (err) {
      // 404 or error means profile doesn't exist yet → new user
      setIsNewUser(true);
      setIsEditing(true);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setForm(prev => ({ ...prev, [name]: type === 'checkbox' ? checked : value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccess(false);

    // Basic validation
    if (!form.age || !form.gender || !form.skinType || !form.dietPreference) {
      setError('Please fill in all required fields (Age, Gender, Skin Type, Diet Preference).');
      return;
    }

    const payload = {
      age: parseInt(form.age),
      gender: form.gender,
      skinType: form.skinType,
      dietPreference: form.dietPreference,
      allergies: form.allergies || '',
      medicalConditions: form.medicalConditions || '',
      pregnant: form.pregnant,
    };

    try {
      setSaving(true);
      if (isNewUser) {
        await createProfile(payload);
      } else {
        await updateProfile(payload);
      }
      setSuccess(true);
      setIsEditing(false);
      setIsNewUser(false);

      // If coming from setup flow, redirect to dashboard after save
      if (isSetup) {
        setTimeout(() => navigate('/dashboard'), 1500);
      }
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to save profile. Please try again.';
      setError(msg);
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="container text-center" style={{ paddingTop: '4rem' }}>
        <p>Loading profile...</p>
      </div>
    );
  }

  return (
    <div className="container" style={{ maxWidth: '640px' }}>

      {/* Setup banner for new users */}
      {isNewUser && (
        <div style={styles.setupBanner}>
          <h3 style={{ marginBottom: '0.4rem' }}>👋 Welcome to NutriCheckAI!</h3>
          <p style={{ fontSize: '0.9rem', opacity: 0.9 }}>
            Please complete your health profile first. This helps our AI give you personalized and accurate ingredient analysis.
          </p>
        </div>
      )}

      <div className="card">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
          <div>
            <h2 style={{ color: 'var(--primary-color)', fontWeight: '700' }}>
              {isNewUser ? 'Complete Your Profile' : 'User Profile'}
            </h2>
            {profileMeta.name && (
              <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem', marginTop: '0.2rem' }}>
                {profileMeta.name} · {profileMeta.email}
              </p>
            )}
          </div>
          {!isNewUser && !isEditing && (
            <button className="btn" onClick={() => setIsEditing(true)}>Edit</button>
          )}
        </div>

        {error && <div className="alert alert-error">{error}</div>}
        {success && (
          <div className="alert alert-success">
            ✅ Profile {isSetup ? 'saved! Redirecting to dashboard...' : 'updated successfully!'}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div style={styles.row}>
            <div className="form-group" style={{ flex: 1 }}>
              <label>Age <span style={{ color: 'red' }}>*</span></label>
              <input
                type="number"
                name="age"
                className="form-control"
                placeholder="e.g. 25"
                value={form.age}
                onChange={handleChange}
                disabled={!isEditing}
                min="1"
                max="120"
              />
            </div>
            <div className="form-group" style={{ flex: 1 }}>
              <label>Gender <span style={{ color: 'red' }}>*</span></label>
              <select
                name="gender"
                className="form-control"
                value={form.gender}
                onChange={handleChange}
                disabled={!isEditing}
              >
                <option value="">Select gender</option>
                <option value="MALE">Male</option>
                <option value="FEMALE">Female</option>
                <option value="OTHER">Other</option>
              </select>
            </div>
          </div>

          <div className="form-group">
            <label>Skin Type <span style={{ color: 'red' }}>*</span></label>
            <select
              name="skinType"
              className="form-control"
              value={form.skinType}
              onChange={handleChange}
              disabled={!isEditing}
            >
              <option value="">Select skin type</option>
              <option value="NORMAL">Normal</option>
              <option value="OILY">Oily</option>
              <option value="DRY">Dry</option>
              <option value="COMBINATION">Combination</option>
              <option value="SENSITIVE">Sensitive</option>
            </select>
          </div>

          <div className="form-group">
            <label>Diet Preference <span style={{ color: 'red' }}>*</span></label>
            <select
              name="dietPreference"
              className="form-control"
              value={form.dietPreference}
              onChange={handleChange}
              disabled={!isEditing}
            >
              <option value="">Select diet preference</option>
              <option value="VEGAN">Vegan</option>
              <option value="VEGETARIAN">Vegetarian</option>
              <option value="NON_VEGETARIAN">Non-Vegetarian</option>
              <option value="KETO">Keto</option>
              <option value="GLUTEN_FREE">Gluten Free</option>
              <option value="NONE">No Preference</option>
            </select>
          </div>

          <div className="form-group">
            <label>Allergies <span style={{ color: 'var(--text-muted)', fontWeight: 400, fontSize: '0.8rem' }}>(optional)</span></label>
            <input
              type="text"
              name="allergies"
              className="form-control"
              placeholder="e.g. Peanuts, Dairy, Shellfish"
              value={form.allergies}
              onChange={handleChange}
              disabled={!isEditing}
            />
          </div>

          <div className="form-group">
            <label>Medical Conditions <span style={{ color: 'var(--text-muted)', fontWeight: 400, fontSize: '0.8rem' }}>(optional)</span></label>
            <input
              type="text"
              name="medicalConditions"
              className="form-control"
              placeholder="e.g. Diabetes, Hypertension"
              value={form.medicalConditions}
              onChange={handleChange}
              disabled={!isEditing}
            />
          </div>

          <div className="form-group" style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
            <input
              type="checkbox"
              id="pregnant"
              name="pregnant"
              checked={form.pregnant}
              onChange={handleChange}
              disabled={!isEditing}
              style={{ width: '18px', height: '18px', cursor: isEditing ? 'pointer' : 'default' }}
            />
            <label htmlFor="pregnant" style={{ margin: 0, cursor: isEditing ? 'pointer' : 'default' }}>
              Currently pregnant
            </label>
          </div>

          {isEditing && (
            <div style={{ display: 'flex', gap: '1rem', marginTop: '1.5rem' }}>
              <button
                type="submit"
                className="btn"
                style={{ flex: 1, padding: '0.75rem' }}
                disabled={saving}
              >
                {saving ? 'Saving...' : isNewUser ? '🚀 Save & Continue' : 'Save Changes'}
              </button>
              {!isNewUser && (
                <button
                  type="button"
                  className="btn"
                  style={{ flex: 1, backgroundColor: 'var(--neutral-color)', padding: '0.75rem' }}
                  onClick={() => { setIsEditing(false); setError(null); }}
                >
                  Cancel
                </button>
              )}
            </div>
          )}
        </form>
      </div>
    </div>
  );
}

const styles = {
  setupBanner: {
    backgroundColor: 'var(--primary-color)',
    color: 'white',
    borderRadius: '10px',
    padding: '1.25rem 1.5rem',
    marginBottom: '1.5rem',
  },
  row: {
    display: 'flex',
    gap: '1rem',
    flexWrap: 'wrap',
  },
};
