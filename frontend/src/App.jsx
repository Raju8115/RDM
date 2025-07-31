import React, { useEffect, useState } from 'react';
import { Routes, Route, Navigate, useLocation, useNavigate } from 'react-router-dom';
import {
  Content,
  Header,
  HeaderName,
  SkipToContent
} from '@carbon/react';

import ProfilePage from './pages/ProfilePage';
import SkillsPage from './pages/SkillsPage';
import SkillsFormPage from './pages/SkillsFormPage';
import ConfirmationPage from './pages/ConfirmationPage';
import ProtectedRoute from './components/ProtectedRoute';
import PersonalInfo from './pages/PersonalInfo';
import AncillaryFormPage from './pages/AcillaryFormPage';
import IBMLogo from './components/IBMLogo';
import './App.css';
import ProfessionalCertificationForm from './pages/ProfessionalCertificationForm';
import HighImpactAssetForm from './pages/HighImpactAssetForm';

function App() {
  const [user, setUser] = useState(undefined); // undefined = loading, null = unauthenticated
  const [isManagerLoaded, setIsManagerLoaded] = useState(false);
  const location = useLocation();
  const navigate = useNavigate();

  // Fetch user session
useEffect(() => {
  const fetchUser = async () => {
    try {
      const res = await fetch(
        "https://rdm-backend-1-raju-a-dev.apps.rm3.7wse.p1.openshiftapps.com/api/user",
        { credentials: "include" }
      );

      const data = await res.json();

      if (data.error === "Not authenticated") {
        window.location.href =
          "https://rdm-backend-1-raju-a-dev.apps.rm3.7wse.p1.openshiftapps.com/login/oauth2/authorization/appid";
        return;
      }

      setUser(data);
    } catch (error) {
      console.error("Auth check failed:", error);
      setUser(null);
    }
  };

  fetchUser();
}, []); // ✅ run only once


  // Redirect if unauthenticated
  useEffect(() => {
    if (user === null) {
      window.location.href = 'https://rdm-backend-1-raju-a-dev.apps.rm3.7wse.p1.openshiftapps.com/oauth2/authorization/appid';
    }
  }, [user]);

  // Fetch isManager and set in sessionStorage, then set isManagerLoaded
  useEffect(() => {
    if (user && user.email) {
      sessionStorage.setItem('userEmail', user.email);
      // Always use email for /profiles/{email}/profile
      const email = user.email;
      fetch(`https://rdm-backend-1-raju-a-dev.apps.rm3.7wse.p1.openshiftapps.com/api/profiles/${email}/profile`, { credentials: 'include' })
        .then(res => res.json())
        .then(profile => {
          const isManager = profile?.employeeType?.isManager === true;
          sessionStorage.setItem('isManager', isManager ? 'true' : 'false');
          
          // Store functionalManager.preferredIdentity in session storage
          if (profile?.functionalManager?.preferredIdentity) {
            sessionStorage.setItem('functionalManagerEmail', profile.functionalManager.preferredIdentity);
            console.log('Stored functionalManagerEmail in session storage:', profile.functionalManager.preferredIdentity);
            console.log('Full functionalManager data:', profile.functionalManager);
          } else {
            console.warn('No functionalManager data found in profile:', profile);
          }
          
          setIsManagerLoaded(true);
        })
        .catch((err) => {
          console.log('[DEBUG] Profile fetch failed, setting isManager to false:', err);
          sessionStorage.setItem('isManager', 'false');
          setIsManagerLoaded(true);
        });
    }
  }, [user]);

  // Redirect to personal-info or manager-ui after successful login, only after isManagerLoaded
  useEffect(() => {
    if (!isManagerLoaded) return;
    const storedEmail = sessionStorage.getItem('userEmail');
    const isManager = sessionStorage.getItem('isManager');
    console.log('[DEBUG] Redirect check:', { storedEmail, isManager });
    if (storedEmail && isManager === 'true') {
        // Redirect to manager_ui (frontend 5174, backend 8083)
        window.location.href = `http://localhost:5174/?email=${encodeURIComponent(storedEmail)}`;
    } else if (storedEmail && isManager === 'false') {
      // Only redirect if on root or login
      if (location.pathname === '/' || location.pathname === '/login') {
        navigate('/personal-info', { replace: true });
      }
    }
  }, [isManagerLoaded, navigate, location]);

  if (user === undefined) {
    return <div style={{ textAlign: 'center' }}>Checking authentication...</div>;
  }

  const isAuthenticated = !!user?.email;

  return (
    <div style={{ height: '100vh', display: 'flex', flexDirection: 'column', overflow: 'hidden' }}>
      {isAuthenticated && (
        <Header aria-label="IBM SkillsPro">
          <SkipToContent />
          <HeaderName prefix="IBM">SkillsPro</HeaderName>
          <div style={{ marginLeft: 'auto', paddingRight: '1rem' }}>
            <IBMLogo />
          </div>
        </Header>
      )}

      <Content style={{ flex: 1, overflow: isAuthenticated ? 'auto' : 'hidden', position: 'relative', height: 'calc(100vh - 48px)' }}>
        <Routes>
          <Route path="/" element={<Navigate to="/personal-info" replace />} />
          <Route path="/personal-info" element={<PersonalInfo onLogout={() => setUser(null)} />} />
          <Route path="/profile" element={<ProfilePage userEmail={user?.email} onLogout={() => setUser(null)} />} />
          <Route
            path="/skills"
            element={
              <ProtectedRoute isAuthenticated={isAuthenticated}>
                <SkillsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/skills/form"
            element={
              <ProtectedRoute isAuthenticated={isAuthenticated}>
                <SkillsFormPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/skills/ancillary-form"
            element={
              <ProtectedRoute isAuthenticated={isAuthenticated}>
                <AncillaryFormPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/skills/professional-form"
            element={
              <ProtectedRoute isAuthenticated={isAuthenticated}>
                <ProfessionalCertificationForm />
              </ProtectedRoute>
            }
          />
          <Route
            path="/skills/high-impact-asset-form"
            element={
              <ProtectedRoute isAuthenticated={isAuthenticated}>
                <HighImpactAssetForm />
              </ProtectedRoute>
            }
          />
          <Route
            path="/confirmation"
            element={
              <ProtectedRoute isAuthenticated={isAuthenticated}>
                <ConfirmationPage />
              </ProtectedRoute>
            }
          />
        </Routes>
      </Content>
    </div>
  );
}

export default App;
