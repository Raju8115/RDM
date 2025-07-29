import React from 'react';
import { Tile } from '@carbon/react';
import './StatsCards.scss';

interface StatsCardsProps {
  directReportees?: number;
  pendingApprovals?: number;
  onPendingApprovalsClick?: () => void;
}

const StatsCards: React.FC<StatsCardsProps> = ({ directReportees = 0, pendingApprovals = 0, onPendingApprovalsClick }) => {
  return (
    <div className="stats-cards-row">
      <Tile className="stats-card">
        <div className="stats-number">{directReportees}</div>
        <div className="stats-label">Direct Reportees</div>
      </Tile>
      <Tile
        className={`stats-card clickable`}
        onClick={onPendingApprovalsClick}
        tabIndex={0}
        role="button"
        aria-label="Go to Pending Approvals"
      >
        <div className="stats-number">{pendingApprovals}</div>
        <div className="stats-label">Pending Approvals</div>
      </Tile>
    </div>
  );
};

export default StatsCards;