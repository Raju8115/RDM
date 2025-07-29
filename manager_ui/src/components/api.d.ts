export {};
declare global {
  const pendingApprovalApi: {
    getAll: () => Promise<any>;
    getById: (id: string | number) => Promise<any>;
    updateStatus: (id: string | number, status: string) => Promise<any>;
  };
} 