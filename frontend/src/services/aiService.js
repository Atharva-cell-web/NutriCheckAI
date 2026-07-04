import api from '../utils/axios';

export const analyzeManual = async (ingredients) => {
  const response = await api.post('/ai/analyze', { ingredients });
  return response.data;
};

export const analyzeImage = async (file) => {
  const formData = new FormData();
  formData.append('image', file);
  
  const response = await api.post('/ai/analyze-image', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
  return response.data;
};
