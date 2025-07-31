import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button, Tag } from '@carbon/react';
import axios from 'axios';
import { userService, fetchIBMUserProfile } from '../services/api';
import '../styles/components/PersonalInfo.scss';

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
  const [professionalCertifications, setProfessionalCertifications] = useState([]);
  const [highImpactAssets, setHighImpactAssets] = useState([]);
  const [projectExperiences, setProjectExperiences] = useState([]);
  const [secondarySkills, setSecondarySkills] = useState([]);
  const [ancillarySkills, setAncillarySkills] = useState([]);

  const BASE_URL = "https://rdm-backend-1-raju-a-dev.apps.rm3.7wse.p1.openshiftapps.com"

  // Fetch all required data for composite score
  useEffect(() => {
    const fetchAllSkillsData = async (email, userId) => {
      try {
        // Profile (for projectExperiences, secondarySkills, ancillarySkills)
        const profileRes = await axios.get(`${BASE_URL}/api/profile/${email}`, { withCredentials: true });
        const profileData = profileRes.data || {};
        setProjectExperiences(profileData.projectExperiences || []);
        setSecondarySkills(profileData.secondarySkills || []);
        setAncillarySkills(profileData.ancillarySkills || []);
        // Professional Certifications
        const profCertRes = await axios.get(`${BASE_URL}/api/professional-certifications/user/${userId}`);
        setProfessionalCertifications(profCertRes.data || []);
        // High Impact Assets
        const highImpactRes = await axios.get(`${BASE_URL}/api/high-impact-assets/user/${userId}`);
        setHighImpactAssets(highImpactRes.data || []);
      } catch (err) {
        // Optionally handle error
      }
    };

    const fetchUserDataAndSkills = async () => {
      try {
        const userResponse = await axios.get(`${BASE_URL}/api/user`, { withCredentials: true });
        console.log(userResponse)
        const email = userResponse.data?.email;
        const userId = userResponse.data?.id;
        if (!email) throw new Error("No email in authenticated user");
        // Fetch IBM profile using the email
        const ibmProfile = await fetchIBMUserProfile(email);
        const content = ibmProfile.content || {};
        setUserData(prev => ({
          ...prev,
          name: content.nameFull || '',
          email: content.preferredIdentity || '',
          slackId: content.preferredSlackUsername || '',
        }));
        if (userId) {
          await fetchAllSkillsData(email, userId);
        }
      } catch (err) {
        console.error('Failed to fetch user data:', err);
        setError('Processing to load user data');
      } finally {
        setLoading(false);
      }
    };
    fetchUserDataAndSkills();
  }, []);

  // Composite score calculations for each section
  const compositeProjectExperience = projectExperiences && projectExperiences.length > 0
    ? projectExperiences
        .map((p) => (typeof p.projectScore === 'number' ? p.projectScore : 0))
        .reduce((a, b) => a + b, 0) / projectExperiences.length
    : 0;

  const compositeProductCertification = secondarySkills && secondarySkills.length > 0
    ? secondarySkills
        .map((s) => (typeof s.certificationScore === 'number' ? s.certificationScore : 0))
        .reduce((a, b) => a + b, 0) / secondarySkills.length
    : 0;

  const compositeThirdPartyCertification = ancillarySkills && ancillarySkills.length > 0
    ? ancillarySkills
        .map((c) => (typeof c.certificationScore === 'number' ? c.certificationScore : 0))
        .reduce((a, b) => a + b, 0) / ancillarySkills.length
    : 0;

  const compositeProfessionalCertification = professionalCertifications && professionalCertifications.length > 0
    ? professionalCertifications
        .map((c) => (typeof c.certificationScore === 'number' ? c.certificationScore : 0))
        .reduce((a, b) => a + b, 0) / professionalCertifications.length
    : 0;

  const compositeHighImpactAssets = highImpactAssets && highImpactAssets.length > 0
    ? highImpactAssets
        .map((a) => (typeof a.impactScore === 'number' ? a.impactScore : 0))
        .reduce((a, b) => a + b, 0) / highImpactAssets.length
    : 0;

  // Overall composite score index (weighted sum divided by 5)
  const overallCompositeScore =
    (
      0.25 * compositeProjectExperience +
      0.15 * compositeProductCertification +
      0.15 * compositeThirdPartyCertification +
      0.25 * compositeHighImpactAssets +
      0.20 * compositeProfessionalCertification
    ) / 5;

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
    await fetch('https://rdm-backend-1-raju-a-dev.apps.rm3.7wse.p1.openshiftapps.com/logout', {
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
          <div className="info-field" style={{ display: 'flex', alignItems: 'center', marginBottom: '1em' }}>
            <label style={{ margin: 0 }}>Composite Score:</label>
            <span style={{ color: '#2563eb', marginLeft: '0.5em' }}>{overallCompositeScore.toFixed(2)}</span>
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
