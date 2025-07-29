// @ts-nocheck
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button } from '@carbon/react';
import axios from 'axios';
import { userService, fetchIBMUserProfile } from './api';
import './PersonalInfo.scss';

export default function ProfilePage({ onLogout }) {
  const navigate = useNavigate();
  const [profileImage, setProfileImage] = useState(null);
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

  function getEmailFromQuery() {
    const params = new URLSearchParams(window.location.search);
    return params.get('email');
  }

  // Fetch user email from backend and then fetch IBM profile
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
      } catch (err) {
        setError('Processing to load user data');
      } finally {
        setLoading(false);
      }
    };
    fetchUserData();
  }, []);

  useEffect(() => {
    const storedImage = localStorage.getItem('profileImage');
    if (storedImage) {
      setProfileImage(storedImage);
    }
  }, []);

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file && file.type.startsWith('image/')) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setProfileImage(reader.result);
        localStorage.setItem('profileImage', reader.result);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleLogout = async () => {
    localStorage.removeItem('profileImage');
    await fetch('http://localhost:8082/logout', {
      method: 'GET',
      credentials: 'include',
    });
    onLogout?.();

  };

  // Construct IBM profile image URL if email is available
  let ibmProfileImageUrl = '';
  if (userData.email) {
    ibmProfileImageUrl = `https://w3-unified-profile-api.ibm.com/v3/image/${encodeURIComponent(userData.email)}?s=0&size=0`;
  }
  let imageToShow = profileImage || (ibmProfileImageUrl ? ibmProfileImageUrl : '/profile-pic.jpg');

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
          <Button kind="tertiary" onClick={handleLogout}>
            Logout
          </Button>
        </div>
      </header>

      <main className="profile-main">
        <div className="profile-photo-section">
          <div className="photo-wrapper">
            <img
              src={imageToShow}
              alt={userData.name ? `${userData.name}'s Profile Photo` : 'Profile Photo'}
              className="profile-photo"
              onError={(e) => { e.target.onerror = null; e.target.src = '/profile-pic.jpg'; }}
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
            <Button kind="primary" size="md" onClick={() => navigate('/profile')}>
              Update Skills Information
            </Button>
          </div>
        </div>
      </main>

      <footer className="profile-footer">
        © IBM Corporation 2025. All rights reserved.
      </footer>
    </div>
  );
} 