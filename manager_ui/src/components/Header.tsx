import React from 'react';
import { Header as CarbonHeader, HeaderName, HeaderNavigation, HeaderMenuItem } from '@carbon/react';

interface HeaderProps {
  activeView: 'dashboard' | 'approvals' | 'profile' | 'userInfo' | 'pendingApprovalDetail';
  setActiveView: (view: 'dashboard' | 'approvals' | 'profile' | 'userInfo' | 'pendingApprovalDetail') => void;
}

const Header: React.FC<HeaderProps> = ({ activeView, setActiveView }) => {
  return (
    <CarbonHeader aria-label="Manager Dashboard" className="custom-navbar">
      <HeaderName href="#" prefix="" className="custom-navbar-title">
        Manager Dashboard
      </HeaderName>
      <HeaderNavigation aria-label="Manager Dashboard">
        <HeaderMenuItem 
          href="#" 
          isCurrentPage={activeView === 'dashboard'}
          onClick={() => setActiveView('dashboard')}
          className="custom-navbar-item"
        >
          Dashboard
        </HeaderMenuItem>
        <HeaderMenuItem 
          href="#" 
          isCurrentPage={activeView === 'approvals'}
          onClick={() => setActiveView('approvals')}
          className="custom-navbar-item"
        >
          Pending Approvals
        </HeaderMenuItem>
      </HeaderNavigation>
    </CarbonHeader>
  );
};

export default Header;