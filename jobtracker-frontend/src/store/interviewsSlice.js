import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';

export const fetchInterviews = createAsyncThunk(
  'interviews/fetchInterviews',
  async (params = {}, { rejectWithValue }) => {
    try {
      const token = localStorage.getItem('token');
      const queryParams = new URLSearchParams(params).toString();
      const url = `/api/v1/interviews${queryParams ? `?${queryParams}` : ''}`;
      
      const response = await fetch(url, {
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        const error = await response.json();
        return rejectWithValue(error.message || 'Failed to fetch interviews');
      }

      return await response.json();
    } catch (error) {
      return rejectWithValue(error.message);
    }
  }
);

const initialState = {
  interviews: [],
  isLoading: false,
  error: null,
};

export const interviewsSlice = createSlice({
  name: 'interviews',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchInterviews.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(fetchInterviews.fulfilled, (state, action) => {
        state.isLoading = false;
        state.interviews = action.payload.data || action.payload;
        state.error = null;
      })
      .addCase(fetchInterviews.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload;
      });
  },
});

export const { clearError } = interviewsSlice.actions;








