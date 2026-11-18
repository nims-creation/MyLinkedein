import axios from 'axios';
export const api=axios.create({baseURL:'/api/v1'});
api.interceptors.request.use(config=>{const token=sessionStorage.getItem('ml_token');if(token)config.headers.Authorization=`Bearer ${token}`;return config;});
export const auth={login:(email,password)=>api.post('/users/auth/login',{email,password}),signup:(name,email,password)=>api.post('/users/auth/signup',{name,email,password})};
export const posts={create:(content)=>api.post('/posts/core',{content}),byUser:(id)=>api.get(`/posts/core/users/${encodeURIComponent(id)}/allPosts`),like:(id)=>api.post(`/posts/likes/${encodeURIComponent(id)}`),unlike:(id)=>api.delete(`/posts/likes/${encodeURIComponent(id)}`)};
export const connections={list:(id)=>api.get(`/connections/core/${encodeURIComponent(id)}/first-degree`),request:(id)=>api.post(`/connections/core/request/${encodeURIComponent(id)}`)};
