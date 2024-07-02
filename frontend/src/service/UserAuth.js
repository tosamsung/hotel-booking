import api from "../util/ApiUtil";
class UserAuthService {
  static async signup(userData) {
    try {
      const response = await api.post(`/auth/signup`, userData);
      return response.data;
    } catch (error) {
      throw error;
    }
  }
  static async refreshToken() {
    try {
      const response = await api.post(`/auth/refreshToken`);
      return response.data;
    } catch (error) {
      throw error;
    }
  }
}
export default UserAuthService;
