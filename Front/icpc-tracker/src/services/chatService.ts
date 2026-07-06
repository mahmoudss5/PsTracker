import { apiClient } from "../config/api.tsx";
import { normalizeApiError } from "./ErrorService";
import type { ChatMessage } from "../types/api.types";

/** Fetch the full message history for a team. */
export async function getTeamMessages(teamId: number): Promise<ChatMessage[]> {
  try {
    const response = await apiClient.get<ChatMessage[]>(
      `/team-messages/team/${teamId}`,
    );
    return response.data;
  } catch (error) {
    throw normalizeApiError(error, "Failed to load chat history");
  }
}
