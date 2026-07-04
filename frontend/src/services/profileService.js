import api from '../utils/axios';

// GET current user's profile
export const getProfile = async () => {
  const response = await api.get('/profile/me');
  return response.data;
};

// POST - create profile (used by new users filling profile for the first time)
export const createProfile = async (profileData) => {
  const response = await api.post('/profile', profileData);
  return response.data;
};

// PUT - update existing profile
export const updateProfile = async (profileData) => {
  const response = await api.put('/profile', profileData);
  return response.data;
};
