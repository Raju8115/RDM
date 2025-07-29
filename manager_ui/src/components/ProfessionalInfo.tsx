import React, { useState, useEffect } from 'react';
import { 
  DataTable,
  Table,
  TableHead,
  TableRow,
  TableHeader,
  TableBody,
  TableCell,
  Button,
  TextInput,
  Select,
  SelectItem,
  Tile,
  Tag,
  ToastNotification
} from '@carbon/react';
import { TrashCan, Add } from '@carbon/icons-react';
import axios from 'axios';
import './ProfessionalInfo.scss';
import { pendingApprovalApi } from './api';

interface ProfessionalData {
  name: string;
  email: string;
  practice: string;
  practiceArea: string;
  practiceProduct: string;
  customerProjects: string;
  selfAssessment: string;
  professionalLevel: string;
  id: string;
}

interface ProjectData {
  id: string;
  sno: number;
  projectTitle: string;
  technologies: string;
  duration: string;
  responsibilities: string;
}

interface SecondarySkill {
  id: string;
  practice: string;
  practiceArea: string;
  products: string;
  duration: string;
  roles: string;
}

interface ProfessionalInfoProps {
  userEmail: string;
  approvalId: string;
  onBack?: () => void;
  removedProductCertifications?: any[];
  removedAncillarySkills?: any[];
  removedProfessionalCertifications?: any[];
  removedBadges?: any[];
  removedHighImpactAssets?: any[];
  removedProjectExperiences?: any[];
  highlightFields?: string[];
  onApprove?: () => void;
  onReject?: (reason: string) => void;
}

const ProfessionalInfo: React.FC<ProfessionalInfoProps> = ({ 
  userEmail, 
  approvalId, 
  onBack, 
  removedProductCertifications = [], 
  removedAncillarySkills = [],
  removedProfessionalCertifications = [],
  removedBadges = [],
  removedHighImpactAssets = [],
  removedProjectExperiences = [],
  highlightFields = [],
  onApprove, 
  onReject 
}) => {
  const [profile, setProfile] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showRejectModal, setShowRejectModal] = useState(false);
  const [rejectReason, setRejectReason] = useState('');
  const [rejectError, setRejectError] = useState('');
  const [showRejectSuccess, setShowRejectSuccess] = useState(false);

  useEffect(() => {
    console.log('userEmail prop in ProfessionalInfo:', userEmail);
    console.log('highlightFields prop in ProfessionalInfo:', highlightFields);
    if (!userEmail) {
      setError('No user email provided. Cannot fetch profile.');
      setLoading(false);
      return;
    }
    const fetchProfile = async () => {
      setLoading(true);
      setError(null);
      try {
        const res = await axios.get(`/api/professional-info/email/${encodeURIComponent(userEmail)}`);
        setProfile(res.data);
        console.log('Fetched profile in ProfessionalInfo:', res.data);
        // Debug: print the full profile object
        console.log('Full profile object:', res.data);
        if (res.data && res.data.badges && res.data.badges.length > 0) {
          console.log('First badge object:', res.data.badges[0]);
        }
        if (!res.data) {
          setError('No profile data found for this user.');
        }
      } catch (err) {
        setProfile(null);
        setError('Failed to fetch profile data.');
      } finally {
        setLoading(false);
      }
    };
    fetchProfile();
  }, [userEmail]);

  if (loading) return <div>Loading...</div>;
  if (error) return <div style={{ color: 'red', padding: '1rem' }}>{error}</div>;
  if (!profile) return <div>No profile data found.</div>;

  // Helper to check if a field path is highlighted
  const isHighlighted = (path: string) => {
    const result = highlightFields.includes(path);
    if (result) {
      console.log('Highlighting path:', path);
    }
    return result;
  };

  // Helper to get highlight styling for newly added fields
  const getHighlightStyle = (path: string) => {
    if (isHighlighted(path)) {
      return { background: '#fff3cd', border: '1px solid #ffeaa7' };
    }
    return {};
  };

  const handleReject = async () => {
    if (!rejectReason.trim()) {
      setRejectError('Rejection reason is required.');
      return;
    }
    if (!approvalId) {
      setRejectError('Internal error: approvalId is missing. Please go back and try again.');
      console.error('[ERROR] Attempted to reject with missing approvalId:', approvalId, 'email:', profile?.email);
      return;
    }
    try {
      // Debug log for rejection
      console.log('[DEBUG] Rejecting user with approvalId:', approvalId, 'email:', profile.email);
      // Call API to reject, passing the user's email and correct approvalId
      await pendingApprovalApi.updateStatus(approvalId, 'Rejected', false, rejectReason, profile.email);
      setShowRejectModal(false);
      setShowRejectSuccess(true);
      setTimeout(() => setShowRejectSuccess(false), 3000);
      if (onReject) onReject(rejectReason);
    } catch (err) {
      setRejectError('Failed to reject.');
    }
  };

  // Render profile fields dynamically (customize as needed)
  return (
    <div className="professional-info">
      {onBack && (
        <Button kind="ghost" size="sm" onClick={onBack} style={{ marginBottom: '1rem' }}>
          Back
        </Button>
      )}
      
      {/* Highlight Legend */}
      {(highlightFields.length > 0 || removedProductCertifications.length > 0 || removedAncillarySkills.length > 0 || 
        removedProfessionalCertifications.length > 0 || removedBadges.length > 0 || removedHighImpactAssets.length > 0 || 
        removedProjectExperiences.length > 0) && (
        <div style={{ 
          marginBottom: '2rem', 
          padding: '1rem', 
          backgroundColor: '#f8f9fa', 
          borderRadius: '8px', 
          border: '1px solid #dee2e6' 
        }}>
          <h3 style={{ margin: '0 0 1rem 0', fontSize: '1.1rem', fontWeight: '600' }}>Change Indicators:</h3>
          <div style={{ display: 'flex', gap: '2rem', flexWrap: 'wrap' }}>
            {highlightFields.length > 0 && (
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <div style={{ 
                  width: '20px', 
                  height: '20px', 
                  backgroundColor: '#fff3cd', 
                  border: '1px solid #ffeaa7',
                  borderRadius: '4px'
                }}></div>
                <span>Newly added or modified fields</span>
              </div>
            )}
            {(removedProductCertifications.length > 0 || removedAncillarySkills.length > 0 || 
              removedProfessionalCertifications.length > 0 || removedBadges.length > 0 || 
              removedHighImpactAssets.length > 0 || removedProjectExperiences.length > 0) && (
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <div style={{ 
                  width: '20px', 
                  height: '20px', 
                  backgroundColor: '#ffeaea', 
                  borderRadius: '4px'
                }}></div>
                <span>Removed items</span>
              </div>
            )}
          </div>
        </div>
      )}
      {showRejectModal && (
        <div className="custom-rejection-modal-overlay">
          <div className="custom-rejection-modal">
            <h2 className="custom-rejection-title">Reject User</h2>
            <textarea
              className="custom-rejection-textarea"
              value={rejectReason}
              onChange={e => setRejectReason(e.target.value)}
              rows={4}
              placeholder="Enter reason for rejection..."
            />
            {rejectError && <div className="custom-rejection-error">{rejectError}</div>}
            <div className="custom-rejection-actions">
              <Button kind="secondary" size="sm" onClick={() => { setShowRejectModal(false); setRejectReason(''); setRejectError(''); }}>Cancel</Button>
              <Button kind="danger" size="sm" onClick={handleReject}>Send</Button>
            </div>
          </div>
        </div>
      )}
      {showRejectSuccess && (
        <ToastNotification
          kind="success"
          title="Rejection sent"
          subtitle="The rejection message was delivered to the user."
          timeout={3000}
          onCloseButtonClick={() => setShowRejectSuccess(false)}
        />
      )}
      {/* Professional Information Table Section */}
      <div className="info-section">
        <h2>Professional Information</h2>
        <div className="professional-info-table-wrapper">
          <Table className="professional-info-table">
            <TableHead>
              <TableRow>
                <TableHeader>Name</TableHeader>
                <TableHeader>Email</TableHeader>
                <TableHeader>Practice</TableHeader>
                <TableHeader>Practice Area</TableHeader>
                <TableHeader>Practice Product/Technology</TableHeader>
                <TableHeader># of Customer Projects</TableHeader>
                <TableHeader>Self Assessment Level</TableHeader>
                <TableHeader>Professional Level</TableHeader>
              </TableRow>
            </TableHead>
                          <TableBody>
                <TableRow>
                  <TableCell style={getHighlightStyle('name')}>{profile.name == null ? '' : typeof profile.name === 'object' ? JSON.stringify(profile.name) : profile.name}</TableCell>
                  <TableCell style={getHighlightStyle('email')}>{profile.email == null ? '' : typeof profile.email === 'object' ? JSON.stringify(profile.email) : profile.email}</TableCell>
                  <TableCell style={getHighlightStyle('practice')}>{profile.practice == null ? '' : typeof profile.practice === 'object' ? JSON.stringify(profile.practice) : profile.practice}</TableCell>
                  <TableCell style={getHighlightStyle('practiceArea')}>{profile.practiceArea == null ? '' : typeof profile.practiceArea === 'object' ? JSON.stringify(profile.practiceArea) : profile.practiceArea}</TableCell>
                  <TableCell style={getHighlightStyle('practiceProduct')}>{profile.practiceProduct == null ? '' : typeof profile.practiceProduct === 'object' ? JSON.stringify(profile.practiceProduct) : profile.practiceProduct}</TableCell>
                  <TableCell style={getHighlightStyle('customerProjects')}>{profile.customerProjects == null ? '' : typeof profile.customerProjects === 'object' ? JSON.stringify(profile.customerProjects) : profile.customerProjects}</TableCell>
                  <TableCell style={getHighlightStyle('selfAssessment')}>{profile.selfAssessment == null ? '' : typeof profile.selfAssessment === 'object' ? JSON.stringify(profile.selfAssessment) : profile.selfAssessment}</TableCell>
                  <TableCell style={getHighlightStyle('professionalLevel')}>{profile.professionalLevel == null ? '' : typeof profile.professionalLevel === 'object' ? JSON.stringify(profile.professionalLevel) : profile.professionalLevel}</TableCell>
              </TableRow>
            </TableBody>
          </Table>
        </div>
      </div>
      <div className="info-section">
        <h2>Project Experience</h2>
        {profile.projectExperiences && profile.projectExperiences.length > 0 ? (
          <DataTable rows={profile.projectExperiences.map((p: any, i: number) => ({ ...p, id: String(p.id ?? i), sno: i + 1 }))} headers={[
            { key: 'sno', header: 'SNo' },
            { key: 'projectTitle', header: 'Project Title' },
            { key: 'technologiesUsed', header: 'Technologies' },
            { key: 'duration', header: 'Duration' },
            { key: 'responsibilities', header: 'Responsibilities' },
            { key: 'clientTierV2', header: 'Client Tier' },
            { key: 'projectComplexity', header: 'Project Complexity' },
            { key: 'projectScore', header: 'Score' },
          ]}>
          {({ rows, headers, getTableProps, getHeaderProps, getRowProps }) => (
              <Table {...getTableProps()} className="project-table">
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
                {rows.map((row, rowIdx) => (
                  <TableRow {...getRowProps({ row })} key={row.id}>
                    {row.cells.map((cell, cellIdx) => {
                      const colKey = headers[cellIdx].key;
                      const path = `projectExperiences[${rowIdx}].${colKey}`;
                      return (
                        <TableCell key={String(cell.id)} style={getHighlightStyle(path)}>
                          {colKey === 'projectScore' && typeof cell.value === 'number' ? (
                            <Tag type="blue" size="lg" style={{ fontWeight: 'bold', fontSize: '1.2em', background: '#e8f0fe', color: '#1976d2', borderRadius: '12px', padding: '0.25em 1em' }}>{cell.value.toFixed(2)}</Tag>
                          ) : (
                            cell.value == null ? '' : typeof cell.value === 'object' ? JSON.stringify(cell.value) : cell.value
                          )}
                        </TableCell>
                      );
                    })}
                  </TableRow>
                ))}
                {removedProjectExperiences && removedProjectExperiences.length > 0 && removedProjectExperiences.map((project: any, idx: number) => (
                  <TableRow key={`removed-project-${idx}`} style={{ background: '#ffeaea' }}>
                    <TableCell style={{ color: '#d32f2f', fontWeight: 'bold' }}>{idx + 1}</TableCell>
                    <TableCell style={{ color: '#d32f2f', fontWeight: 'bold' }}>{project.projectTitle || ''}</TableCell>
                    <TableCell style={{ color: '#d32f2f', fontWeight: 'bold' }}>{project.technologiesUsed || ''}</TableCell>
                    <TableCell style={{ color: '#d32f2f', fontWeight: 'bold' }}>{project.duration || ''}</TableCell>
                    <TableCell style={{ color: '#d32f2f', fontWeight: 'bold' }}>{project.responsibilities || ''}</TableCell>
                    <TableCell style={{ color: '#d32f2f', fontWeight: 'bold' }}>{project.clientTierV2 || ''}</TableCell>
                    <TableCell style={{ color: '#d32f2f', fontWeight: 'bold' }}>{project.projectComplexity || ''}</TableCell>
                    <TableCell style={{ color: '#d32f2f', fontWeight: 'bold' }}>{typeof project.projectScore === 'number' ? project.projectScore.toFixed(2) : ''}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}
        </DataTable>
        ) : <div>No project experience found.</div>}
        {/* Composite Score Display for Project Experience */}
        <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '1rem', alignItems: 'center' }}>
          <span style={{ fontWeight: 'bold', fontSize: '1.2em', color: '#161616', marginRight: '0.5em' }}>
            Composite Score:
          </span>
          <Tag type="green" size="lg" style={{ fontWeight: 'bold', fontSize: '1.2em', background: '#e6f4ea', color: '#137333', borderRadius: '12px', padding: '0.25em 1em' }}>
            {profile.projectExperiences && profile.projectExperiences.length > 0
              ? (
                  (
                    profile.projectExperiences
                      .map((p: any) => (typeof p.projectScore === 'number' ? p.projectScore : 0))
                      .reduce((a: number, b: number) => a + b, 0) / profile.projectExperiences.length
                  ).toFixed(2)
                )
              : "0.00"
            }
          </Tag>
        </div>
      </div>
      {/* Update Secondary Skills Table Columns */}
      <div className="info-section">
        <h2>Product Certifications</h2>
        {profile.secondarySkills && profile.secondarySkills.length > 0 ? (
          <DataTable rows={profile.secondarySkills.map((s: any, i: number) => ({ ...s, id: String(s.id ?? i) }))} headers={[
            { key: 'practice', header: 'Practice' },
            { key: 'practiceArea', header: 'Practice Area' },
            { key: 'productsTechnologies', header: 'Products / Technologies' },
            { key: 'duration', header: 'Duration' },
            { key: 'roles', header: 'Roles & Responsibilities' },
            { key: 'certificationLevel', header: 'Certification Level' },
            { key: 'recencyOfCertification', header: 'Recency of Certification' },
            { key: 'certificationScore', header: 'Score' },
          ]}>
          {({ rows, headers, getTableProps, getHeaderProps, getRowProps }) => (
              <Table {...getTableProps()} className="skills-table">
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
                {rows.map((row, rowIdx) => (
                  <TableRow {...getRowProps({ row })} key={row.id}>
                    {row.cells.map((cell, cellIdx) => {
                      const colKey = headers[cellIdx].key;
                      const path = `secondarySkills[${rowIdx}].${colKey}`;
                      return (
                        <TableCell key={String(cell.id)} style={getHighlightStyle(path)}>
                          {colKey === 'certificationScore' && typeof cell.value === 'number' ? (
                            <Tag type="blue" size="lg" style={{ fontWeight: 'bold', fontSize: '1.2em', background: '#e8f0fe', color: '#1976d2', borderRadius: '12px', padding: '0.25em 1em' }}>{cell.value.toFixed(2)}</Tag>
                          ) : (
                            cell.value == null ? '' : typeof cell.value === 'object' ? JSON.stringify(cell.value) : cell.value
                          )}
                        </TableCell>
                      );
                    })}
                  </TableRow>
                ))}
                {removedProductCertifications && removedProductCertifications.length > 0 && removedProductCertifications.map((skill: any, idx: number) => (
                  <TableRow key={`removed-${idx}`} style={{ background: '#ffeaea' }}>
                    <TableCell style={{ color: '#d32f2f', fontWeight: 'bold' }}>{skill.practice || ''}</TableCell>
                    <TableCell style={{ color: '#d32f2f', fontWeight: 'bold' }}>{skill.practiceArea || ''}</TableCell>
                    <TableCell style={{ color: '#d32f2f', fontWeight: 'bold' }}>{skill.productsTechnologies || ''}</TableCell>
                    <TableCell style={{ color: '#d32f2f', fontWeight: 'bold' }}>{skill.duration || ''}</TableCell>
                    <TableCell style={{ color: '#d32f2f', fontWeight: 'bold' }}>{skill.roles || ''}</TableCell>
                    <TableCell style={{ color: '#d32f2f', fontWeight: 'bold' }}>{skill.certificationLevel || ''}</TableCell>
                    <TableCell style={{ color: '#d32f2f', fontWeight: 'bold' }}>{skill.recencyOfCertification || ''}</TableCell>
                    <TableCell style={{ color: '#d32f2f', fontWeight: 'bold' }}>{typeof skill.certificationScore === 'number' ? skill.certificationScore.toFixed(2) : ''}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}
        </DataTable>
        ) : <div>No Product Certifications found.</div>}
        {/* Composite Score Display for Product Certifications */}
        <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '1rem', alignItems: 'center' }}>
          <span style={{ fontWeight: 'bold', fontSize: '1.2em', color: '#161616', marginRight: '0.5em' }}>
            Composite Score:
          </span>
          <Tag type="green" size="lg" style={{ fontWeight: 'bold', fontSize: '1.2em', background: '#e6f4ea', color: '#137333', borderRadius: '12px', padding: '0.25em 1em' }}>
            {profile.secondarySkills && profile.secondarySkills.length > 0
              ? (
                  (
                    profile.secondarySkills
                      .map((s: any) => (typeof s.certificationScore === 'number' ? s.certificationScore : 0))
                      .reduce((a: number, b: number) => a + b, 0) / profile.secondarySkills.length
                  ).toFixed(2)
                )
              : "0.00"
            }
          </Tag>
        </div>
      </div>
      {/* Update 3rd Party Certifications Table Columns */}
      <div className="info-section">
        <h2>3rd Party Certifications</h2>
        {profile.ancillarySkills && profile.ancillarySkills.length > 0 ? (
          <DataTable rows={profile.ancillarySkills.map((c: any, i: number) => ({ ...c, id: String(c.id ?? i) }))} headers={[
            { key: 'technology', header: 'Technology' },
            { key: 'product', header: 'Product' },
            { key: 'certified', header: 'Certified' },
            { key: 'certificationLink', header: 'Certification Link' },
            { key: 'certificationLevel', header: 'Certification Level' },
            { key: 'recencyOfCertification', header: 'Recency of Certification' },
            { key: 'certificationScore', header: 'Score' },
          ]}>
          {({ rows, headers, getTableProps, getHeaderProps, getRowProps }) => (
              <Table {...getTableProps()} className="cert-table">
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
                {rows.map((row, rowIdx) => (
                  <TableRow {...getRowProps({ row })} key={row.id}>
                    {row.cells.map((cell, cellIdx) => {
                      const colKey = headers[cellIdx].key;
                      const path = `ancillarySkills[${rowIdx}].${colKey}`;
                      return (
                        <TableCell key={String(cell.id)} style={getHighlightStyle(path)}>
                          {colKey === 'certified' ? (
                            <span className={`certified-badge ${cell.value ? 'certified-yes' : 'certified-no'}`}>{cell.value ? 'Yes' : 'No'}</span>
                          ) : colKey === 'certificationLink' ? (
                            cell.value ? (
                              <a href={cell.value} target="_blank" rel="noopener noreferrer" className="certification-link">View</a>
                            ) : '-' 
                          ) : colKey === 'certificationScore' && typeof cell.value === 'number' ? (
                            <Tag type="blue" size="lg" style={{ fontWeight: 'bold', fontSize: '1.2em', background: '#e8f0fe', color: '#1976d2', borderRadius: '12px', padding: '0.25em 1em' }}>{cell.value.toFixed(2)}</Tag>
                          ) : (
                            cell.value == null ? '' : typeof cell.value === 'object' ? JSON.stringify(cell.value) : cell.value
                          )}
                        </TableCell>
                      );
                    })}
                  </TableRow>
                ))}
                {removedAncillarySkills && removedAncillarySkills.length > 0 && removedAncillarySkills.map((cert: any, idx: number) => (
                  <TableRow key={`removed-ancillary-${idx}`} style={{ background: '#ffeaea' }}>
                    <TableCell style={{ color: '#d32f2f', fontWeight: 'bold' }}>{cert.technology || ''}</TableCell>
                    <TableCell style={{ color: '#d32f2f', fontWeight: 'bold' }}>{cert.product || ''}</TableCell>
                    <TableCell style={{ color: '#d32f2f', fontWeight: 'bold' }}>{cert.certified ? 'Yes' : 'No'}</TableCell>
                    <TableCell style={{ color: '#d32f2f', fontWeight: 'bold' }}>{cert.certificationLink || ''}</TableCell>
                    <TableCell style={{ color: '#d32f2f', fontWeight: 'bold' }}>{cert.certificationLevel || ''}</TableCell>
                    <TableCell style={{ color: '#d32f2f', fontWeight: 'bold' }}>{cert.recencyOfCertification || ''}</TableCell>
                    <TableCell style={{ color: '#d32f2f', fontWeight: 'bold' }}>{typeof cert.certificationScore === 'number' ? cert.certificationScore.toFixed(2) : ''}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}
        </DataTable>
        ) : <div>No 3rd Party Certifications found.</div>}
        {/* Composite Score Display for 3rd Party Certifications */}
        <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '1rem', alignItems: 'center' }}>
          <span style={{ fontWeight: 'bold', fontSize: '1.2em', color: '#161616', marginRight: '0.5em' }}>
            Composite Score:
          </span>
          <Tag type="green" size="lg" style={{ fontWeight: 'bold', fontSize: '1.2em', background: '#e6f4ea', color: '#137333', borderRadius: '12px', padding: '0.25em 1em' }}>
            {profile.ancillarySkills && profile.ancillarySkills.length > 0
              ? (
                  (
                    profile.ancillarySkills
                      .map((c: any) => (typeof c.certificationScore === 'number' ? c.certificationScore : 0))
                      .reduce((a: number, b: number) => a + b, 0) / profile.ancillarySkills.length
                  ).toFixed(2)
                )
              : "0.00"
            }
          </Tag>
        </div>
      </div>
      <div className="info-section">
        <h2>Badges</h2>
        {profile.badges && profile.badges.length > 0 ? (
          (() => {
            // Find the first non-empty expiry date from badges
            const globalExpiryDate = profile.badges.find((b: any) => b.credentialExpiryDate)?.credentialExpiryDate;
            return (
              <Table>
                <TableHead>
                  <TableRow>
                    <TableHeader>Credential Title</TableHeader>
                    <TableHeader>Credential Type</TableHeader>
                    <TableHeader>Learning Source</TableHeader>
                    <TableHeader>Credential Status</TableHeader>
                    <TableHeader>Credential Date</TableHeader>
                    <TableHeader>Credential Expiry Date</TableHeader>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {profile.badges.map((badge: any, idx: number) => (
                    <TableRow key={badge.credentialOrderId || idx}>
                      <TableCell style={getHighlightStyle(`badges[${idx}].credentialLabel`)}>
                        {badge.credentialLabel && badge.credentialLabel.trim() && badge.credentialLabel.trim() !== '-' ? (
                          <b>{badge.credentialLabel.replace(/\s*\(.*\)$/, '')}</b>
                        ) : badge.credentialTitle && badge.credentialTitle.trim() && badge.credentialTitle.trim() !== '-' ? (
                          <b>{badge.credentialTitle.replace(/\s*\(.*\)$/, '')}</b>
                        ) : badge.credential_label && badge.credential_label.trim() && badge.credential_label.trim() !== '-' ? (
                          <b>{badge.credential_label.replace(/\s*\(.*\)$/, '')}</b>
                        ) : badge.credential_title && badge.credential_title.trim() && badge.credential_title.trim() !== '-' ? (
                          <b>{badge.credential_title.replace(/\s*\(.*\)$/, '')}</b>
                        ) : '-'}
                      </TableCell>
                      <TableCell style={getHighlightStyle(`badges[${idx}].credentialType`)}>{badge.credentialType}</TableCell>
                      <TableCell style={getHighlightStyle(`badges[${idx}].learningSource`)}>{badge.learningSource}</TableCell>
                      <TableCell style={getHighlightStyle(`badges[${idx}].credentialStatus`)}>{badge.credentialStatus}</TableCell>
                      <TableCell style={getHighlightStyle(`badges[${idx}].credentialDate`)}>{badge.credentialDate ? badge.credentialDate.substring(0, 10) : '-'}</TableCell>
                      <TableCell style={getHighlightStyle(`badges[${idx}].credentialExpiryDate`)}>{badge.credentialExpiryDate ? badge.credentialExpiryDate.substring(0, 10) : (globalExpiryDate ? globalExpiryDate.substring(0, 10) : '-')}</TableCell>
                    </TableRow>
                  ))}
                  {removedBadges && removedBadges.length > 0 && removedBadges.map((badge: any, idx: number) => (
                    <TableRow key={`removed-badge-${idx}`} style={{ background: '#ffeaea' }}>
                      <TableCell style={{ color: '#d32f2f', fontWeight: 'bold' }}>
                        {badge.credentialLabel && badge.credentialLabel.trim() && badge.credentialLabel.trim() !== '-' ? (
                          badge.credentialLabel.replace(/\s*\(.*\)$/, '')
                        ) : badge.credentialTitle && badge.credentialTitle.trim() && badge.credentialTitle.trim() !== '-' ? (
                          badge.credentialTitle.replace(/\s*\(.*\)$/, '')
                        ) : badge.credential_label && badge.credential_label.trim() && badge.credential_label.trim() !== '-' ? (
                          badge.credential_label.replace(/\s*\(.*\)$/, '')
                        ) : badge.credential_title && badge.credential_title.trim() && badge.credential_title.trim() !== '-' ? (
                          badge.credential_title.replace(/\s*\(.*\)$/, '')
                        ) : '-'}
                      </TableCell>
                      <TableCell style={{ color: '#d32f2f', fontWeight: 'bold' }}>{badge.credentialType}</TableCell>
                      <TableCell style={{ color: '#d32f2f', fontWeight: 'bold' }}>{badge.learningSource}</TableCell>
                      <TableCell style={{ color: '#d32f2f', fontWeight: 'bold' }}>{badge.credentialStatus}</TableCell>
                      <TableCell style={{ color: '#d32f2f', fontWeight: 'bold' }}>{badge.credentialDate ? badge.credentialDate.substring(0, 10) : '-'}</TableCell>
                      <TableCell style={{ color: '#d32f2f', fontWeight: 'bold' }}>{badge.credentialExpiryDate ? badge.credentialExpiryDate.substring(0, 10) : '-'}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            );
          })()
        ) : <div>No badges found.</div>}
      </div>
      {/* Professional Certifications Section */}
      <div className="info-section">
        <h2>Professional Certifications</h2>
        {profile.professionalCertifications && profile.professionalCertifications.length > 0 ? (
          <Table>
            <TableHead>
              <TableRow>
                <TableHeader>Title</TableHeader>
                <TableHeader>Certified</TableHeader>
                <TableHeader>Certification Link</TableHeader>
                <TableHeader>Certification Level</TableHeader>
                <TableHeader>Score</TableHeader>
              </TableRow>
            </TableHead>
            <TableBody>
              {profile.professionalCertifications.map((cert: any, idx: number) => (
                <TableRow key={String(cert.id ?? idx)}>
                  <TableCell style={getHighlightStyle(`professionalCertifications[${idx}].title`)}>{cert.title}</TableCell>
                  <TableCell style={getHighlightStyle(`professionalCertifications[${idx}].certified`)}>{cert.certified ? <Tag type="green">Yes</Tag> : <Tag type="gray">No</Tag>}</TableCell>
                  <TableCell style={getHighlightStyle(`professionalCertifications[${idx}].certificationLink`)}>{cert.certificationLink ? <a href={cert.certificationLink} target="_blank" rel="noopener noreferrer">View</a> : '-'}</TableCell>
                  <TableCell style={getHighlightStyle(`professionalCertifications[${idx}].certificationLevel`)}>{cert.certificationLevel || '-'}</TableCell>
                  <TableCell style={getHighlightStyle(`professionalCertifications[${idx}].certificationScore`)}>{typeof cert.certificationScore === 'number' ? (
                    <Tag type="blue" size="lg" style={{ fontWeight: 'bold', fontSize: '1.2em', background: '#e8f0fe', color: '#1976d2', borderRadius: '12px', padding: '0.25em 1em' }}>{cert.certificationScore.toFixed(2)}</Tag>
                  ) : <Tag type="gray" size="md">-</Tag>}</TableCell>
                </TableRow>
              ))}
              {removedProfessionalCertifications && removedProfessionalCertifications.length > 0 && removedProfessionalCertifications.map((cert: any, idx: number) => (
                <TableRow key={`removed-professional-${idx}`} style={{ background: '#ffeaea' }}>
                  <TableCell style={{ color: '#d32f2f', fontWeight: 'bold' }}>{cert.title || ''}</TableCell>
                  <TableCell style={{ color: '#d32f2f', fontWeight: 'bold' }}>{cert.certified ? 'Yes' : 'No'}</TableCell>
                  <TableCell style={{ color: '#d32f2f', fontWeight: 'bold' }}>{cert.certificationLink || ''}</TableCell>
                  <TableCell style={{ color: '#d32f2f', fontWeight: 'bold' }}>{cert.certificationLevel || ''}</TableCell>
                  <TableCell style={{ color: '#d32f2f', fontWeight: 'bold' }}>{typeof cert.certificationScore === 'number' ? cert.certificationScore.toFixed(2) : ''}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        ) : <div>No professional certifications found.</div>}
        {/* Composite Score for Professional Certifications */}
        <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '1rem', alignItems: 'center' }}>
          <span style={{ fontWeight: 'bold', fontSize: '1.2em', color: '#161616', marginRight: '0.5em' }}>
            Composite Score:
          </span>
          <Tag type="green" size="lg" style={{ fontWeight: 'bold', fontSize: '1.2em', background: '#e6f4ea', color: '#137333', borderRadius: '12px', padding: '0.25em 1em' }}>
            {profile.professionalCertifications && profile.professionalCertifications.length > 0
              ? (
                  (
                    profile.professionalCertifications
                      .map((c: any) => (typeof c.certificationScore === 'number' ? c.certificationScore : 0))
                      .reduce((a: number, b: number) => a + b, 0) / profile.professionalCertifications.length
                  ).toFixed(2)
                )
              : "0.00"
            }
          </Tag>
        </div>
      </div>
      {/* High Impact Assets Section */}
      <div className="info-section">
        <h2>High Impact Assets and Accelerators</h2>
        {profile.highImpactAssets && profile.highImpactAssets.length > 0 ? (
          <Table>
            <TableHead>
              <TableRow>
                <TableHeader>Title</TableHeader>
                <TableHeader>Business Impact</TableHeader>
                <TableHeader>Visibility / Adoption</TableHeader>
                <TableHeader>Description</TableHeader>
                <TableHeader>Score</TableHeader>
              </TableRow>
            </TableHead>
            <TableBody>
              {profile.highImpactAssets.map((asset: any, idx: number) => (
                <TableRow key={String(asset.id ?? idx)}>
                  <TableCell style={getHighlightStyle(`highImpactAssets[${idx}].title`)}>{asset.title}</TableCell>
                  <TableCell style={getHighlightStyle(`highImpactAssets[${idx}].businessImpact`)}>{asset.businessImpact}</TableCell>
                  <TableCell style={getHighlightStyle(`highImpactAssets[${idx}].visibilityAdoption`)}>{asset.visibilityAdoption}</TableCell>
                  <TableCell style={getHighlightStyle(`highImpactAssets[${idx}].description`)}>{asset.description}</TableCell>
                  <TableCell style={getHighlightStyle(`highImpactAssets[${idx}].impactScore`)}>{typeof asset.impactScore === 'number' ? (
                    <Tag type="blue" size="lg" style={{ fontWeight: 'bold', fontSize: '1.2em', background: '#e8f0fe', color: '#1976d2', borderRadius: '12px', padding: '0.25em 1em' }}>{asset.impactScore.toFixed(2)}</Tag>
                  ) : <Tag type="gray" size="md">-</Tag>}</TableCell>
                </TableRow>
              ))}
              {removedHighImpactAssets && removedHighImpactAssets.length > 0 && removedHighImpactAssets.map((asset: any, idx: number) => (
                <TableRow key={`removed-high-impact-${idx}`} style={{ background: '#ffeaea' }}>
                  <TableCell style={{ color: '#d32f2f', fontWeight: 'bold' }}>{asset.title || ''}</TableCell>
                  <TableCell style={{ color: '#d32f2f', fontWeight: 'bold' }}>{asset.businessImpact || ''}</TableCell>
                  <TableCell style={{ color: '#d32f2f', fontWeight: 'bold' }}>{asset.visibilityAdoption || ''}</TableCell>
                  <TableCell style={{ color: '#d32f2f', fontWeight: 'bold' }}>{asset.description || ''}</TableCell>
                  <TableCell style={{ color: '#d32f2f', fontWeight: 'bold' }}>{typeof asset.impactScore === 'number' ? asset.impactScore.toFixed(2) : ''}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        ) : <div>No high impact assets or accelerators found.</div>}
        {/* Composite Score for High Impact Assets */}
        <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '1rem', alignItems: 'center' }}>
          <span style={{ fontWeight: 'bold', fontSize: '1.2em', color: '#161616', marginRight: '0.5em' }}>
            Composite Score:
          </span>
          <Tag type="green" size="lg" style={{ fontWeight: 'bold', fontSize: '1.2em', background: '#e6f4ea', color: '#137333', borderRadius: '12px', padding: '0.25em 1em' }}>
            {profile.highImpactAssets && profile.highImpactAssets.length > 0
              ? (
                  (
                    profile.highImpactAssets
                      .map((a: any) => (typeof a.impactScore === 'number' ? a.impactScore : 0))
                      .reduce((a: number, b: number) => a + b, 0) / profile.highImpactAssets.length
                  ).toFixed(2)
                )
              : "0.00"
            }
          </Tag>
        </div>
      </div>
      {/* Approve/Reject Actions at the end of the form, inside .professional-info */}
      {(onApprove || onReject) && (
        <div className="approve-reject-actions" style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-end', gap: '1.5rem', marginTop: '3rem', marginBottom: '2.5rem', width: '100%' }}>
          <div style={{ display: 'flex', gap: '1.5rem', justifyContent: 'flex-end', width: '100%' }}>
            {onApprove && (
              <Button kind="primary" size="lg" className="approve-btn" onClick={onApprove}>
                Approve
              </Button>
            )}
            {onReject && (
              <Button kind="danger" size="lg" className="reject-btn" onClick={() => setShowRejectModal(true)}>
                Reject
              </Button>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default ProfessionalInfo;