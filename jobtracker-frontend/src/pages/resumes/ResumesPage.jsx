import React, { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { fetchResumes } from '../../store/resumesSlice';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../../components/common/Card';
import { Button } from '../../components/common/Button';
import { Loading } from '../../components/common/Loading';

const ResumesPage = () => {
  const dispatch = useDispatch();
  const { resumes, isLoading } = useSelector((state) => state.resumes);

  useEffect(() => {
    dispatch(fetchResumes());
  }, [dispatch]);

  if (isLoading) {
    return <Loading />;
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Resumes</h1>
          <p className="text-gray-600">Manage your resume files</p>
        </div>
        <Button>Upload Resume</Button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {resumes.length > 0 ? (
          resumes.map((resume) => (
            <Card key={resume.id}>
              <CardHeader>
                <CardTitle>{resume.title}</CardTitle>
                <CardDescription>
                  Uploaded {new Date(resume.createdAt).toLocaleDateString()}
                </CardDescription>
              </CardHeader>
              <CardContent>
                <p className="text-sm text-gray-600 mb-4">{resume.description}</p>
                <div className="flex space-x-2">
                  <Button size="sm" variant="outline">View</Button>
                  <Button size="sm" variant="outline">Download</Button>
                </div>
              </CardContent>
            </Card>
          ))
        ) : (
          <Card className="col-span-full">
            <CardContent className="p-12 text-center">
              <h3 className="text-lg font-semibold text-gray-900 mb-2">No resumes uploaded</h3>
              <p className="text-gray-600 mb-4">Upload your first resume to get started</p>
              <Button>Upload Resume</Button>
            </CardContent>
          </Card>
        )}
      </div>
    </div>
  );
};

export default ResumesPage;








