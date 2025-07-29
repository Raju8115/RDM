import React, { useState, useEffect } from 'react';
import { Content } from '@carbon/react';
import Header from './components/Header';
import StatsCards from './components/StatsCards';
import PendingApprovals from './components/PendingApprovals';
import ProfessionalInfo from './components/ProfessionalInfo';
// @ts-ignore
import PersonalInfo from './components/PersonalInfo.jsx';
import Dashboard from './components/Dashboard';
import './Dashboard.scss';
import { pendingApprovalApi } from './components/api';

interface ApprovalData {
  id: string;
  name: string;
  email: string;
  businessLocation: string;
  status: string;
}

interface HeaderProps {
  activeView: 'dashboard' | 'approvals' | 'userInfo' | 'profile' | 'pendingApprovalDetail';
  setActiveView: (view: 'dashboard' | 'approvals' | 'userInfo' | 'profile' | 'pendingApprovalDetail') => void;
}

const App: React.FC = () => {
  const [activeView, setActiveView] = useState<'dashboard' | 'approvals' | 'userInfo' | 'profile' | 'pendingApprovalDetail'>('dashboard');
  const [pendingApprovalsCount, setPendingApprovalsCount] = useState(0);
  const [profileImage, setProfileImage] = useState<string>('https://images.pexels.com/photos/2379004/pexels-photo-2379004.jpeg?auto=compress&cs=tinysrgb&w=200&h=200&dpr=2');
  const [selectedUser, setSelectedUser] = useState<any>(null);
  const [selectedApprovalId, setSelectedApprovalId] = useState<string | null>(null);
  const [selectedApproval, setSelectedApproval] = useState<any>(null);
  const [removedProductCertifications, setRemovedProductCertifications] = useState<any[]>([]);
  const [removedAncillarySkills, setRemovedAncillarySkills] = useState<any[]>([]);
  const [removedProfessionalCertifications, setRemovedProfessionalCertifications] = useState<any[]>([]);
  const [removedBadges, setRemovedBadges] = useState<any[]>([]);
  const [removedHighImpactAssets, setRemovedHighImpactAssets] = useState<any[]>([]);
  const [removedProjectExperiences, setRemovedProjectExperiences] = useState<any[]>([]);
  const [highlightFields, setHighlightFields] = useState<string[]>([]);
  const [managerEmail, setManagerEmail] = useState<string | null>(null);

  // Extract manager email from URL parameters on app initialization
  useEffect(() => {
    const urlParams = new URLSearchParams(window.location.search);
    const emailParam = urlParams.get('email');
    if (emailParam) {
      setManagerEmail(emailParam);
      console.log('Manager email extracted from URL:', emailParam);
      // Store in session storage for API calls
      sessionStorage.setItem('managerEmail', emailParam);
    } else {
      console.warn('No manager email found in URL parameters');
    }
  }, []);

  // Fetch pending approvals count on app initialization
  useEffect(() => {
    const fetchPendingApprovalsCount = async () => {
      try {
        const res = await pendingApprovalApi.getAll();
        const pendingCount = res.data.filter((a: any) => a.status === 'Pending').length;
        setPendingApprovalsCount(pendingCount);
      } catch (err) {
        console.error('Failed to fetch pending approvals count:', err);
        setPendingApprovalsCount(0);
      }
    };
    
    fetchPendingApprovalsCount();
  }, []);

  useEffect(() => {
    localStorage.setItem('activeView', activeView);
  }, [activeView]);

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      const reader = new FileReader();
      reader.onload = (ev) => {
        if (ev.target && typeof ev.target.result === 'string') {
          setProfileImage(ev.target.result);
        }
      };
      reader.readAsDataURL(e.target.files[0]);
    }
  };

  const handlePreviewUser = async (row: any) => {
    // If row has id, fetch full approval details
    let approvalId = row && row.id;
    if (!approvalId && row && row.email) {
      // Find the approval by email from the approvals list
      const approval = (window as any).approvals?.find((a: any) => a.email?.toLowerCase() === row.email?.toLowerCase());
      approvalId = approval?.id;
    }
    if (approvalId) {
      try {
        const res = await pendingApprovalApi.getById(approvalId);
        setSelectedApproval({ ...res.data, id: approvalId });
        setRemovedProductCertifications(res.data.removedProductCertifications || []);
        setRemovedAncillarySkills(res.data.removedAncillarySkills || []);
        setRemovedProfessionalCertifications(res.data.removedProfessionalCertifications || []);
        setRemovedBadges(res.data.removedBadges || []);
        setRemovedHighImpactAssets(res.data.removedHighImpactAssets || []);
        setRemovedProjectExperiences(res.data.removedProjectExperiences || []);
        setHighlightFields(res.data.highlightFields || []);
        console.log('Received highlightFields from API:', res.data.highlightFields);
        setActiveView('pendingApprovalDetail');
      } catch (err) {
        setSelectedApproval(null);
        setRemovedProductCertifications([]);
        setRemovedAncillarySkills([]);
        setRemovedProfessionalCertifications([]);
        setRemovedBadges([]);
        setRemovedHighImpactAssets([]);
        setRemovedProjectExperiences([]);
        setHighlightFields([]);
        setActiveView('pendingApprovalDetail');
      }
    } else {
      setSelectedUser({ name: row.name, email: row.email });
      setActiveView('userInfo');
    }
  };

  const handleApprove = async () => {
    if (selectedApproval && selectedApproval.id) {
      await pendingApprovalApi.updateStatus(selectedApproval.id, 'Approved', selectedApproval.updated);
      setActiveView('approvals');
    }
  };
  const handleReject = async (reason?: string) => {
    if (selectedApproval && selectedApproval.id) {
      await pendingApprovalApi.updateStatus(selectedApproval.id, 'Rejected', selectedApproval.updated, reason);
      setActiveView('approvals');
    }
  };

  const renderContent = () => {
    switch (activeView) {
      case 'dashboard':
        return <Dashboard setActiveView={setActiveView} pendingApprovalsCount={pendingApprovalsCount} />;
      case 'approvals':
        return <PendingApprovals onPreviewUser={handlePreviewUser} setPendingApprovalsCount={setPendingApprovalsCount} onBack={() => setActiveView('dashboard')} />;
      case 'pendingApprovalDetail':
        return selectedApproval ? (
          <ProfessionalInfo
            userEmail={selectedApproval.email}
            approvalId={selectedApproval.id}
            onBack={() => setActiveView('approvals')}
            removedProductCertifications={removedProductCertifications}
            removedAncillarySkills={removedAncillarySkills}
            removedProfessionalCertifications={removedProfessionalCertifications}
            removedBadges={removedBadges}
            removedHighImpactAssets={removedHighImpactAssets}
            removedProjectExperiences={removedProjectExperiences}
            highlightFields={highlightFields}
            onApprove={handleApprove}
            onReject={handleReject}
          />
        ) : null;
      case 'userInfo':
        return selectedUser ? <ProfessionalInfo userEmail={selectedUser.email} approvalId={''} onBack={() => setActiveView('approvals')} onApprove={() => {}} onReject={() => {}} /> : null;
      default:
        return null;
    }
  };

  return (
    <>
      <Header activeView={activeView} setActiveView={setActiveView} />
      {(activeView === 'profile' || activeView === 'userInfo') && (
        <header className="profile-header">
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <img src="https://upload.wikimedia.org/wikipedia/commons/5/51/IBM_logo.svg" alt="IBM Logo" className="ibm-logo" />
            <span style={{ fontWeight: 600, fontSize: '1rem', marginLeft: '0.5rem' }}>IBM SkillsPro</span>
          </div>
          <h2 className="profile-title">Profile</h2>
          <div className="header-right">
            <button style={{
              border: '1px solid #1976d2',
              background: 'transparent',
              color: '#1976d2',
              borderRadius: 4,
              padding: '0.5rem 2rem',
              fontWeight: 500,
              fontSize: '1rem',
              cursor: 'pointer',
              minWidth: 100
            }}>Logout</button>
          </div>
        </header>
      )}
      <Content>{renderContent()}</Content>
    </>
  );
};

export default App;