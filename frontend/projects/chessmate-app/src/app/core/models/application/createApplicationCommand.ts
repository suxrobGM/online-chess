export interface CreateApplicationCommand {
  name: string;
  description: string | null;
  projectId: string;
}
