import React from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { createJob } from '../../store/jobsSlice';
import { toast } from 'react-toastify';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../../components/common/Card';
import { Button } from '../../components/common/Button';
import { Input } from '../../components/common/Input';
import { Loading } from '../../components/common/Loading';

const schema = yup.object({
  title: yup.string().required('Job title is required'),
  company: yup.string().required('Company name is required'),
  location: yup.string().required('Location is required'),
  applicationDate: yup.date().required('Application date is required'),
  jobDescription: yup.string(),
  requirements: yup.string(),
  benefits: yup.string(),
  notes: yup.string(),
});

const JobCreatePage = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { isLoading } = useSelector((state) => state.jobs);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: yupResolver(schema),
  });

  const onSubmit = async (data) => {
    try {
      await dispatch(createJob(data)).unwrap();
      toast.success('Job application created successfully!');
      navigate('/jobs');
    } catch (error) {
      toast.error(error || 'Failed to create job application');
    }
  };

  return (
    <div className="max-w-2xl mx-auto space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Add New Job Application</h1>
        <p className="text-gray-600">Track your job application details</p>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Job Information</CardTitle>
          <CardDescription>
            Fill in the details of your job application
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label htmlFor="title" className="block text-sm font-medium text-gray-700">
                  Job Title *
                </label>
                <Input
                  id="title"
                  placeholder="e.g. Software Engineer"
                  {...register('title')}
                  className={errors.title ? 'border-red-500' : ''}
                />
                {errors.title && (
                  <p className="mt-1 text-sm text-red-600">{errors.title.message}</p>
                )}
              </div>

              <div>
                <label htmlFor="company" className="block text-sm font-medium text-gray-700">
                  Company *
                </label>
                <Input
                  id="company"
                  placeholder="e.g. Google"
                  {...register('company')}
                  className={errors.company ? 'border-red-500' : ''}
                />
                {errors.company && (
                  <p className="mt-1 text-sm text-red-600">{errors.company.message}</p>
                )}
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label htmlFor="location" className="block text-sm font-medium text-gray-700">
                  Location *
                </label>
                <Input
                  id="location"
                  placeholder="e.g. San Francisco, CA"
                  {...register('location')}
                  className={errors.location ? 'border-red-500' : ''}
                />
                {errors.location && (
                  <p className="mt-1 text-sm text-red-600">{errors.location.message}</p>
                )}
              </div>

              <div>
                <label htmlFor="applicationDate" className="block text-sm font-medium text-gray-700">
                  Application Date *
                </label>
                <Input
                  id="applicationDate"
                  type="date"
                  {...register('applicationDate')}
                  className={errors.applicationDate ? 'border-red-500' : ''}
                />
                {errors.applicationDate && (
                  <p className="mt-1 text-sm text-red-600">{errors.applicationDate.message}</p>
                )}
              </div>
            </div>

            <div>
              <label htmlFor="jobDescription" className="block text-sm font-medium text-gray-700">
                Job Description
              </label>
              <textarea
                id="jobDescription"
                rows={4}
                className="input"
                placeholder="Describe the job role and responsibilities..."
                {...register('jobDescription')}
              />
            </div>

            <div>
              <label htmlFor="requirements" className="block text-sm font-medium text-gray-700">
                Requirements
              </label>
              <textarea
                id="requirements"
                rows={3}
                className="input"
                placeholder="List the job requirements..."
                {...register('requirements')}
              />
            </div>

            <div>
              <label htmlFor="benefits" className="block text-sm font-medium text-gray-700">
                Benefits
              </label>
              <textarea
                id="benefits"
                rows={3}
                className="input"
                placeholder="List the job benefits..."
                {...register('benefits')}
              />
            </div>

            <div>
              <label htmlFor="notes" className="block text-sm font-medium text-gray-700">
                Notes
              </label>
              <textarea
                id="notes"
                rows={3}
                className="input"
                placeholder="Add any additional notes..."
                {...register('notes')}
              />
            </div>

            <div className="flex justify-end space-x-4">
              <Button
                type="button"
                variant="outline"
                onClick={() => navigate('/jobs')}
              >
                Cancel
              </Button>
              <Button type="submit" disabled={isLoading}>
                {isLoading ? <Loading size="sm" /> : 'Create Job Application'}
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
};

export default JobCreatePage;








