export const BASE_URL = "http://localhost:8080";

export const saveToken = (data) => {
    localStorage.setItem('access_token', btoa(data.access_token));
    localStorage.setItem('refresh_token', btoa(data.refresh_token));
}

export const isUserLoggedIn = () => {
    return localStorage.getItem("access_token");
}