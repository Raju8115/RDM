import React, { useState, useEffect } from 'react';
import { 
  DataTable, 
  Table, 
  TableHead, 
  TableRow, 
  TableHeader, 
  TableBody, 
  TableCell,
  Search,
  Tile,
  Button
} from '@carbon/react';
import { Edit } from '@carbon/icons-react';
import './PendingApprovals.scss';
import { pendingApprovalApi } from './api';

interface ApprovalData {
  id: string;
  name: string;
  email: string;
  businessLocation: string;
  status: string;
}

interface PendingApprovalsProps {
  onPreviewUser?: (user: any) => void;
  setPendingApprovalsCount: (count: number) => void;
  onBack?: () => void;
}

const PendingApprovals: React.FC<PendingApprovalsProps> = ({ onPreviewUser, setPendingApprovalsCount, onBack }) => {
  const [searchValue, setSearchValue] = useState('');
  const [approvals, setApprovals] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [viewedApprovals, setViewedApprovals] = useState<string[]>(() => {
    // Load viewed approvals from localStorage
    const stored = localStorage.getItem('viewedApprovals');
    return stored ? JSON.parse(stored) : [];
  });
  const [, forceUpdate] = useState(0); // for re-render

  useEffect(() => {
    const fetchApprovals = async () => {
      setLoading(true);
      try {
        console.log('Fetching pending approvals from:', 'http://localhost:8083/api/pending-approvals');
        const res = await pendingApprovalApi.getAll();
        console.log('Received approvals response:', res);
        console.log('Approvals data:', res.data);
        console.log('Number of approvals:', res.data?.length || 0);
        
        if (Array.isArray(res.data)) {
          res.data.forEach((approval: any, index: number) => {
            console.log(`Approval ${index}:`, {
              id: approval.id,
              name: approval.name,
              email: approval.email,
              status: approval.status,
              managerEmail: approval.managerEmail
            });
          });
        }
        
        setApprovals(res.data);
        // Update pending approvals count
        const pendingCount = res.data.filter((a: any) => a.status === 'Pending').length;
        console.log('Pending approvals count:', pendingCount);
        setPendingApprovalsCount(pendingCount);
      } catch (err: any) {
        console.error('Error fetching approvals:', err);
        console.error('Error response:', err.response?.data);
        console.error('Error status:', err.response?.status);
        setApprovals([]);
        setPendingApprovalsCount(0);
      } finally {
        setLoading(false);
      }
    };
    fetchApprovals();
  }, [setPendingApprovalsCount]);

  // Save viewedApprovals to localStorage whenever it changes
  useEffect(() => {
    localStorage.setItem('viewedApprovals', JSON.stringify(viewedApprovals));
  }, [viewedApprovals]);

  const handleApprove = async (id: string) => {
    const approval = approvals.find(a => a.id === id);
    const updatedFlag = approval?.updated || false;
    await pendingApprovalApi.updateStatus(id, 'Approved', updatedFlag);
    setApprovals((prev) => {
      // Do NOT remove the user from the list. Update their status to 'Approved'.
      const updated = prev.map(a => a.id === id ? { ...a, status: 'Approved', updated: false } : a);
      setPendingApprovalsCount(updated.filter((a: any) => a.status === 'Pending' || (a.status === 'Approved' && a.updated)).length);
      return updated;
    });
  };
  const handleReject = async (id: string) => {
    const approval = approvals.find(a => a.id === id);
    const email = approval?.email;
    await pendingApprovalApi.updateStatus(id, 'Rejected', false, undefined, email);
    setApprovals((prev) => {
      // Remove the rejected user from the list
      const updated = prev.filter(a => a.id !== id);
      setPendingApprovalsCount(updated.filter((a: any) => a.status === 'Pending' || (a.status === 'Approved' && a.updated)).length);
      return updated;
    });
  };

  const handlePreviewUserInternal = (row: any) => {
    console.log('Row object in PendingApprovals handlePreviewUserInternal:', row);
    if (Array.isArray(row.cells)) {
      console.log('Cells array in row:', row.cells);
      row.cells.forEach((cell: any, idx: number) => {
        console.log(`Cell ${idx}:`, cell);
      });
    }
    // Extract email and name from cells
    let email = '';
    let name = '';
    if (Array.isArray(row.cells)) {
      for (const cell of row.cells) {
        if (cell.info && cell.info.header === 'email') email = cell.value;
        if (cell.info && cell.info.header === 'name') name = cell.value;
      }
    }
    if (!viewedApprovals.includes(String(row.id))) {
      setViewedApprovals(prev => {
        const updated = [...prev, String(row.id)];
        setTimeout(() => forceUpdate(n => n + 1), 0); // force re-render after state update
        return updated;
      });
    }
    if (!email) {
      alert('No email found in row. Available keys: ' + Object.keys(row).join(', '));
    }
    // Find the full approval object by id
    const approval = approvals.find(a => String(a.id) === String(row.id));
    if (onPreviewUser && approval) {
      onPreviewUser(approval);
    } else if (onPreviewUser) {
      onPreviewUser({ name, email });
    }
  };

  const headers = [
    { key: 'name', header: 'Name' },
    { key: 'email', header: 'Email' },
    { key: 'status', header: 'Status' },
    { key: 'actions', header: 'Actions' },
  ];

  // Show all except rejected
  const filteredApprovals = approvals.filter(approval =>
    approval.status !== 'Rejected' && (
      approval.name?.toLowerCase().includes(searchValue.toLowerCase()) ||
      approval.email?.toLowerCase().includes(searchValue.toLowerCase())
    )
  ).map(a => ({ ...a, id: String(a.id) }));

  return (
    <div className="pending-approvals">
      {/* Back button below the navbar, top left of content */}
      {onBack && (
        <div style={{ display: 'flex', alignItems: 'center', margin: '1.5rem 0 1rem 0' }}>
          <button className="back-btn" onClick={onBack} style={{ background: 'none', border: 'none', color: '#1976d2', fontWeight: 600, fontSize: '1rem', cursor: 'pointer', padding: 0 }}>
            ← Back
          </button>
        </div>
      )}
      <div className="approvals-header">
        <Search
          size="lg"
          placeholder="Search"
          labelText=""
          value={searchValue}
          onChange={(e) => setSearchValue(e.target.value)}
          className="approvals-search"
        />
      </div>
      <Tile className="approvals-section">
        <div className="section-header">
          <h2>Pending Approvals</h2>
        </div>
        {loading ? <div>Loading...</div> : (
        <DataTable rows={filteredApprovals} headers={headers}>
          {({ rows, headers, getTableProps, getHeaderProps, getRowProps }) => (
            <Table {...getTableProps()} className="approvals-table">
              <TableHead>
                <TableRow>
                  {headers.map((header) => (
                    <TableHeader {...getHeaderProps({ header })} key={header.key}>
                      {header.header}
                    </TableHeader>
                  ))}
                </TableRow>
              </TableHead>
              <TableBody>
                {rows.map((row) => (
                  <TableRow {...getRowProps({ row })} key={row.id}>
                    {row.cells.map((cell) => (
                      <TableCell key={cell.id}>
                        {cell.info.header === 'actions' ? (
                          <Button size="sm" kind="secondary" onClick={() => handlePreviewUserInternal(row)}>
                            View
                          </Button>
                        ) : cell.info.header === 'status' ? (
                          (() => {
                            const approval = approvals.find(a => String(a.id) === String(row.id));
                            if (!approval) return cell.value;
                            if (approval.status === 'Approved' && approval.updated) return <span style={{ color: 'green', fontWeight: 600 }}>Approved/Updated</span>;
                            if (approval.status === 'Approved') return <span style={{ color: 'green', fontWeight: 600 }}>Approved</span>;
                            if (approval.status === 'Pending' && approval.updated) return <span style={{ color: '#f1c21b', fontWeight: 600 }}>Updated</span>;
                            if (approval.status === 'Pending') return 'Pending';
                            return cell.value;
                          })()
                        ) : cell.value}
                      </TableCell>
                    ))}
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}
        </DataTable>
        )}
      </Tile>
    </div>
  );
};

export default PendingApprovals;