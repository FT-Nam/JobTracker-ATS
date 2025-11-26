import React from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { Button } from '../common/Button';
import { Input } from '../common/Input';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../common/Card';

const schema = yup.object().shape({
  title: yup.string().required('Job title is required'),
  company: yup.string().required('Company is required'),
  location: yup.string().required('Location is required'),
  salaryMin: yup.number().min(0, 'Salary must be positive'),
  salaryMax: yup.number().min(0, 'Salary must be positive'),
  jobUrl: yup.string().url('Must be a valid URL'),
});

const JobForm = ({ onSubmit, initialData, isLoading }) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: yupResolver(schema),
    defaultValues: initialData,
  });

  return (
    <Card>
      <CardHeader>
        <CardTitle>Job Information</CardTitle>
        <CardDescription>
          Enter the details of the job you're applying for.
        </CardDescription>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div>
            <label htmlFor="title" className="block text-sm font-medium text-foreground mb-1">
              Job Title
            </label>
            <Input
              id="title"
              type="text"
              placeholder="e.g., Senior Software Engineer"
              {...register('title')}
              className={errors.title ? 'border-destructive' : ''}
            />
            {errors.title && (
              <p className="text-sm text-destructive mt-1">{errors.title.message}</p>
            )}
          </div>

          <div>
            <label htmlFor="company" className="block text-sm font-medium text-foreground mb-1">
              Company
            </label>
            <Input
              id="company"
              type="text"
              placeholder="e.g., Google"
              {...register('company')}
              className={errors.company ? 'border-destructive' : ''}
            />
            {errors.company && (
              <p className="text-sm text-destructive mt-1">{errors.company.message}</p>
            )}
          </div>

          <div>
            <label htmlFor="location" className="block text-sm font-medium text-foreground mb-1">
              Location
            </label>
            <Input
              id="location"
              type="text"
              placeholder="e.g., San Francisco, CA"
              {...register('location')}
              className={errors.location ? 'border-destructive' : ''}
            />
            {errors.location && (
              <p className="text-sm text-destructive mt-1">{errors.location.message}</p>
            )}
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label htmlFor="salaryMin" className="block text-sm font-medium text-foreground mb-1">
                Min Salary
              </label>
              <Input
                id="salaryMin"
                type="number"
                placeholder="e.g., 100000"
                {...register('salaryMin')}
                className={errors.salaryMin ? 'border-destructive' : ''}
              />
              {errors.salaryMin && (
                <p className="text-sm text-destructive mt-1">{errors.salaryMin.message}</p>
              )}
            </div>

            <div>
              <label htmlFor="salaryMax" className="block text-sm font-medium text-foreground mb-1">
                Max Salary
              </label>
              <Input
                id="salaryMax"
                type="number"
                placeholder="e.g., 150000"
                {...register('salaryMax')}
                className={errors.salaryMax ? 'border-destructive' : ''}
              />
              {errors.salaryMax && (
                <p className="text-sm text-destructive mt-1">{errors.salaryMax.message}</p>
              )}
            </div>
          </div>

          <div>
            <label htmlFor="jobUrl" className="block text-sm font-medium text-foreground mb-1">
              Job URL
            </label>
            <Input
              id="jobUrl"
              type="url"
              placeholder="e.g., https://careers.google.com/jobs/123"
              {...register('jobUrl')}
              className={errors.jobUrl ? 'border-destructive' : ''}
            />
            {errors.jobUrl && (
              <p className="text-sm text-destructive mt-1">{errors.jobUrl.message}</p>
            )}
          </div>

          <div className="flex justify-end space-x-2">
            <Button type="button" variant="outline">
              Cancel
            </Button>
            <Button type="submit" disabled={isLoading}>
              {isLoading ? 'Saving...' : 'Save Job'}
            </Button>
          </div>
        </form>
      </CardContent>
    </Card>
  );
};

export default JobForm;







