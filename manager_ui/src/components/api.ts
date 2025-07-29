import axios from 'axios';

const BASE_URL = 'http://localhost:8083/api';

/**
 * @typedef {Object} PendingApprovalApi
 * @property {() => Promise<any>} getAll
 * @property {(id: string | number) => Promise<any>} getById
 * @property {(id: string | number, status: string) => Promise<any>} updateStatus
 */

// Helper function to get manager email from session storage
const getManagerEmail = (): string | null => {
  return sessionStorage.getItem('managerEmail');
};

// Helper function to build URL with manager email parameter
const buildUrlWithManagerEmail = (endpoint: string): string => {
  const managerEmail = getManagerEmail();
  if (managerEmail) {
    const separator = endpoint.includes('?') ? '&' : '?';
    return `${BASE_URL}${endpoint}${separator}managerEmail=${encodeURIComponent(managerEmail)}`;
  }
  return `${BASE_URL}${endpoint}`;
};

/** @type {PendingApprovalApi} */
export const pendingApprovalApi = {
  getAll: (): Promise<any> => {
    const url = buildUrlWithManagerEmail('/pending-approvals');
    console.log('Fetching pending approvals from:', url);
    return axios.get(url, { withCredentials: true });
  },
  getById: (id: string | number): Promise<any> => {
    const url = buildUrlWithManagerEmail(`/pending-approvals/${id}`);
    console.log('Fetching approval by ID from:', url);
    return axios.get(url, { withCredentials: true });
  },
  updateStatus: (id: string | number, status: string, updated: boolean = false, rejectionReason?: string, email?: string): Promise<any> => {
    const url = buildUrlWithManagerEmail(`/pending-approvals/${id}`);
    console.log('Updating approval status at:', url);
    return axios.put(url, { status, updated, ...(rejectionReason ? { rejectionReason } : {}), ...(email ? { email } : {}) }, { withCredentials: true });
  },
};

// Add userService and fetchIBMUserProfile for PersonalInfo.jsx compatibility
export const userService = {
  getProfileById: (id: string | number): Promise<any> => axios.get(`https://w3-unified-profile-api.ibm.com/v3/profiles/${encodeURIComponent(String(id))}/profile`),
  register: (userData: any): Promise<any> => axios.post('https://w3-unified-profile-api.ibm.com/v3/register', userData),
  updateProfile: (email: string, userData: any): Promise<any> => axios.put(`${BASE_URL}/profile/${email}`, userData, { withCredentials: true }),
};

export async function fetchIBMUserProfile(email: string): Promise<any> {
  try {
    const res = await axios.get(
      `https://w3-unified-profile-api.ibm.com/v3/profiles/${encodeURIComponent(email)}/profile`
    );
    return res.data;
  } catch (error) {
    console.error('Failed to fetch IBM user profile:', error);
    throw error;
  }
} 