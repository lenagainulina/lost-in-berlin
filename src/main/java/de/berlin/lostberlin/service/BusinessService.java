package de.berlin.lostberlin.service;

import de.berlin.lostberlin.model.business.client.BusinessPostDto;
import de.berlin.lostberlin.model.business.client.BusinessShortDao;
import de.berlin.lostberlin.model.business.client.BusinessUpdateDto;
import de.berlin.lostberlin.model.business.client.BusinessUpdatePhotoDto;
import de.berlin.lostberlin.model.business.persistence.Business;

import java.util.List;

public interface BusinessService {
    /**
     * Gets a list of shortened businesses profiles for a given location.
     *
     * @param serviceLocation location, where a business offers its service
     * @return complete list of {@link BusinessShortDao} objects associated with given service location.
     * business profiles don't include contact information as well as account details (username/password)
     * and information internal to the application (id, created at, updated at).
     */
    List<BusinessShortDao> retrieveBusinessByLocation(String serviceLocation);

    /**
     * Saves a newly created business profile to business repository
     *
     * @param businessProfile newly created business profile
     * @return {@link Business} object with entered business profile details
     */
    Business saveNewlyCreatedBusinessProfile(BusinessPostDto businessProfile);

    /**
     * Gets a business profile by given id
     *
     * @param id business id
     * @return {@link Business} object with given id
     */
    Business retrieveBusinessById(Long id);

    /**
     * Updates given business profile by given id
     *
     * @param id business id
     * @param businessProfile update for a business profile
     * @return {@link Business} object with updated business profile details
     */
    Business saveUpdatedBusinessProfile(Long id, BusinessUpdateDto businessProfile);

    /**
     *
     * @param id business id
     * @param photoFilePath updated profile photo filepath
     * @return (@link Business) object with updated profile photo file path.
     */
    Business savePartiallyUpdatedBusinessProfile(Long id, BusinessUpdatePhotoDto photoFilePath);

}