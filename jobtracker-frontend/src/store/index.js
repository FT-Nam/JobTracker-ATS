import { configureStore } from '@reduxjs/toolkit';
import { authSlice } from './authSlice';
import { jobsSlice } from './jobsSlice';
import { usersSlice } from './usersSlice';
import { companiesSlice } from './companiesSlice';
import { skillsSlice } from './skillsSlice';
import { resumesSlice } from './resumesSlice';
import { interviewsSlice } from './interviewsSlice';
import { notificationsSlice } from './notificationsSlice';

export const store = configureStore({
  reducer: {
    auth: authSlice.reducer,
    jobs: jobsSlice.reducer,
    users: usersSlice.reducer,
    companies: companiesSlice.reducer,
    skills: skillsSlice.reducer,
    resumes: resumesSlice.reducer,
    interviews: interviewsSlice.reducer,
    notifications: notificationsSlice.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: ['persist/PERSIST'],
      },
    }),
});








