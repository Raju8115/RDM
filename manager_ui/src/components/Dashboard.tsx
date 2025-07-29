import React, { useState, useEffect } from 'react';
import { Button } from '@carbon/react';
import './Dashboard.scss';
import StatsCards from './StatsCards';
import axios from 'axios';
import { fetchIBMUserProfile } from './api';

interface DashboardProps {
  setActiveView: (view: 'dashboard' | 'approvals' | 'profile' | 'userInfo') => void;
  pendingApprovalsCount: number;
}

function getEmailFromQuery() {
  const params = new URLSearchParams(window.location.search);
  return params.get('email');
}

const Dashboard: React.FC<DashboardProps> = ({ setActiveView, pendingApprovalsCount }) => {
  const [profileImage, setProfileImage] = useState<string | null>(null);
  const [userData, setUserData] = useState({
    id: null,
    name: '',
    email: '',
    slackId: '',
    primarySkills: [],
    secondarySkills: [],
    ancillarySkills: []
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // New: W3 profile image URL state
  const [w3ProfileImageUrl, setW3ProfileImageUrl] = useState<string>('');

  useEffect(() => {
    const fetchUserData = async () => {
      try {
        let email = getEmailFromQuery();
        if (!email) {
          const userResponse = await axios.get('/api/user', { withCredentials: true });
          email = userResponse.data?.email;
        }
        if (!email) throw new Error('No email in authenticated user');
        const ibmProfile = await fetchIBMUserProfile(email);
        const content = ibmProfile.content || {};
        setUserData(prev => ({
          ...prev,
          name: content.nameFull || content.nameDisplay || '',
          email: content.preferredIdentity || email,
          slackId: content.preferredSlackUsername || '',
        }));
        // Set W3 profile image URL
        setW3ProfileImageUrl(`https://w3-unified-profile-api.ibm.com/v3/image/${encodeURIComponent(email)}?s=0&size=0`);
      } catch (err) {
        setError('Processing to load user data');
      } finally {
        setLoading(false);
      }
    };
    fetchUserData();
  }, []);

  // Remove localStorage logic for profile image

  let imageToShow = profileImage || w3ProfileImageUrl || '/profile-pic.jpg';

  const handleImageError = (e: React.SyntheticEvent<HTMLImageElement, Event>) => {
    e.currentTarget.onerror = null;
    e.currentTarget.src = '/profile-pic.jpg';
  };

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files && e.target.files[0];
    if (file && file.type.startsWith('image/')) {
      const reader = new FileReader();
      reader.onloadend = () => {
        if (typeof reader.result === 'string') {
          setProfileImage(reader.result);
          localStorage.setItem('profileImage', reader.result);
        }
      };
      reader.readAsDataURL(file);
    }
  };

  const handlePendingApprovalsClick = () => {
    setActiveView('approvals');
  };

  if (loading) {
    return <div className="p-4">Loading...</div>;
  }
  if (error) {
    return <div className="p-4 text-red-500">{error}</div>;
  }

  return (
    <div className="profile-container">
      <header className="profile-header">
        <div className="header-left" />
        <h2 className="profile-title">Profile</h2>
        <div className="header-right">
          <Button kind="tertiary">
            Logout
          </Button>
        </div>
      </header>
      <main className="profile-main">
        <div className="profile-row">
          <div className="profile-photo-section">
            <div className="photo-wrapper">
              <img
                src={imageToShow}
                alt={userData.name ? `${userData.name}'s Profile Photo` : 'Profile Photo'}
                className="profile-photo"
                onError={handleImageError}
              />
              <label htmlFor="upload-input" className="upload-icon">⬆</label>
              <input
                type="file"
                id="upload-input"
                accept="image/*"
                onChange={handleImageChange}
                hidden
              />
            </div>
          </div>
          <div className="user-info-card">
            <h5>User Information</h5>
            <div className="info-field">
              <label>Email:</label>
              <span>{userData.email || 'Not set'}</span>
            </div>
            <div className="info-field">
              <label>Name:</label>
              <span>{userData.name || 'Not set'}</span>
            </div>
            <div className="info-field">
              <label>Slack ID:</label>
              <span>{userData.slackId || 'Not set'}</span>
            </div>
            <div className="skills-button">
              <Button kind="primary" size="md">
                Update Skills Information
              </Button>
            </div>
          </div>
        </div>
        {/* StatsCards now always below the profile row */}
        <StatsCards directReportees={50} pendingApprovals={pendingApprovalsCount} onPendingApprovalsClick={handlePendingApprovalsClick} />
      </main>
      <footer className="profile-footer">
        © IBM Corporation 2025. All rights reserved.
      </footer>
    </div>
  );
};

export default Dashboard; 