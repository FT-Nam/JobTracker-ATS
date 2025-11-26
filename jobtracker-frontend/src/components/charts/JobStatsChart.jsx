import React from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../common/Card';

const JobStatsChart = ({ data }) => {
  const stats = [
    { label: 'Applied', value: data?.applied || 0, color: 'bg-blue-500' },
    { label: 'Interview', value: data?.interview || 0, color: 'bg-yellow-500' },
    { label: 'Offer', value: data?.offer || 0, color: 'bg-green-500' },
    { label: 'Rejected', value: data?.rejected || 0, color: 'bg-red-500' },
  ];

  const total = stats.reduce((sum, stat) => sum + stat.value, 0);

  return (
    <Card>
      <CardHeader>
        <CardTitle>Job Application Statistics</CardTitle>
        <CardDescription>
          Overview of your job application progress
        </CardDescription>
      </CardHeader>
      <CardContent>
        <div className="space-y-4">
          {stats.map((stat) => (
            <div key={stat.label} className="flex items-center justify-between">
              <div className="flex items-center space-x-2">
                <div className={`w-3 h-3 rounded-full ${stat.color}`} />
                <span className="text-sm font-medium">{stat.label}</span>
              </div>
              <div className="flex items-center space-x-2">
                <span className="text-sm font-semibold">{stat.value}</span>
                <span className="text-xs text-muted-foreground">
                  ({total > 0 ? Math.round((stat.value / total) * 100) : 0}%)
                </span>
              </div>
            </div>
          ))}
        </div>
      </CardContent>
    </Card>
  );
};

export default JobStatsChart;







