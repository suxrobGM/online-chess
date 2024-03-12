export interface UpdateProjectCommand {
  id: string;
  name?: string;
  description?: string | null;
  clientId?: string;
}
