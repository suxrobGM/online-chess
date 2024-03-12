export interface UpdateApplicationCommand {
  id: string;
  name?: string;
  description?: string | null;
  projectId?: string;
}
