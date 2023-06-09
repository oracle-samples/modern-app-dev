/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { h } from 'preact';
import 'ojs/ojbutton';
import 'ojs/ojformlayout';
import 'ojs/ojselectsingle';
import 'ojs/ojinputsearch';
import { ProviderList } from 'components/provider-list';
import { useCallback, useMemo, useState } from 'preact/hooks';
import { useNavigate } from 'react-router-dom';
import { useI18n } from 'hooks/useI18n';
import useTitle from 'hooks/useTitle';
import { SmartFilterSearch } from 'components/layouts/smart-filter-search';
import { PatientFooter } from '../patientFooter';
import { Filter, Search } from 'components/search';
import useFilters from 'hooks/useFilters';

export function Providers() {
  const navigate = useNavigate();
  const providersI18n = useI18n().patient.providers;
  const providerFilters = useMemo<Filter[]>(
    () => [
      {
        key: 'speciality',
        field: 'speciality',
        label: 'Speciality',
        type: 'enum',
        selected: false,
        value: 'Physician',
        values: [
          { label: providersI18n.physician(), value: 'Physician' },
          { label: providersI18n.cardiologist(), value: 'Cardiologist' },
          { label: providersI18n.boneSpecialist(), value: 'BoneSpecialist' },
          { label: providersI18n.neurologist(), value: 'Neurologist' },
          { label: providersI18n.ophthalmologist(), value: 'Ophthalmologist' }
        ]
      },
      {
        key: 'location',
        field: 'location',
        label: 'Location',
        type: 'enum',
        selected: false,
        value: 'Chicago',
        values: ['Chicago', 'Los Angeles', 'Boston', 'Seattle', 'San Francisco', 'New York'].map((city) => {
          return { value: city, label: city };
        })
      }
    ],
    [providersI18n]
  );

  const [filters, setFilters, searchFilter, setSearchFilter, filter] = useFilters(providerFilters);
  useTitle(providersI18n.title());

  const onFiltersUpdated = useCallback(
    (filters: Filter[]) => {
      setFilters(filters);
    },
    [setFilters]
  );

  const onSearchUpdated = useCallback(
    (search: string) => {
      setSearchFilter(search);
    },
    [setSearchFilter]
  );

  const onSelection = useCallback(
    (id: number) => {
      navigate(`/patient/providers/${id}`);
    },
    [navigate]
  );

  return (
    <SmartFilterSearch
      title={providersI18n.title()}
      subtitle={providersI18n.description()}
      filters={filters}
      onFiltersUpdated={onFiltersUpdated}
      onSearchUpdated={onSearchUpdated}
      placeholder={providersI18n.searchSpecialist()}
      content={<ProviderList onSelection={onSelection} nameSearch={searchFilter} filter={filter} />}
      footer={<PatientFooter />}
    />
  );
}
