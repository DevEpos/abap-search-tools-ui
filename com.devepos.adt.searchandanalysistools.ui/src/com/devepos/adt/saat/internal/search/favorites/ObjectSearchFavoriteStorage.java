package com.devepos.adt.saat.internal.search.favorites;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMLResource;

import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavoritesFactory;
import com.devepos.adt.saat.model.objectsearchfavorites.util.ObjectSearchFavoritesResourceFactoryImpl;

/**
 * Handles the serializing/deserializing of object search favorites
 *
 * @author stockbal
 */
public class ObjectSearchFavoriteStorage {
  private static final String FAVORITES = "favorites.xml";

  /**
   * Serializes the object search favorites
   */
  public static void serialize() {
    serialize(SearchAndAnalysisPlugin.getDefault().getFavoriteManager(), getFavoritesFilePath());
  }

  /**
   * Serializes the given favorites to the users' default plugin location
   *
   * @param favorites the favorites to serialize
   * @param filePath
   */
  public static void serialize(final IObjectSearchFavorites favorites, final String filePath) {
    if (favorites == null || filePath == null) {
      return;
    }
    final IObjectSearchFavoritesFactory factory = IObjectSearchFavoritesFactory.eINSTANCE;
    final com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorites eFavorites = factory
        .createObjectSearchFavorites();
    favorites.getFavorites().forEach(f -> eFavorites.getObjectSearchFavorites().add(f));

    // Obtain a new resource set
    final Resource.Factory resourceFactory = new ObjectSearchFavoritesResourceFactoryImpl();
    try {
      final Resource resource = resourceFactory.createResource(URI.createFileURI(filePath));
      final EList<EObject> resourceContents = resource.getContents();
      resourceContents.add(eFavorites);
      final Map<String, Object> options = createEmfResourceOptions();
      resource.save(options);
    } catch (final IllegalArgumentException | IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Deserializes the object search favorites into the default location of the
   * Plugin
   *
   * @param favorites the favorite manager where the favorites should deserialized
   *                  into
   */
  public static void deserialize(final IObjectSearchFavorites favorites) {
    deserialize(favorites, getFavoritesFilePath());
  }

  /**
   * Deserializes the object search favorites from the file system into the passed
   * {@link IObjectSearchFavorites}
   *
   * @param favorites the favorites where the stored favorites should be
   *                  deserialized into
   * @param filePath  the path to the file where the favorites should be read from
   */
  public static void deserialize(final IObjectSearchFavorites favorites, final String filePath) {
    if (favorites == null || filePath == null || !new File(filePath).exists()) {
      return;
    }
    // Obtain a new resource set
    final Resource.Factory factory = new ObjectSearchFavoritesResourceFactoryImpl();
    try {
      final Resource resource = factory.createResource(URI.createFileURI(filePath));
      final Map<String, Object> options = createEmfResourceOptions();
      resource.load(options);
      final EList<EObject> resourceContents = resource.getContents();
      // List of favorites is the root
      if (resourceContents != null && resourceContents.size() == 1) {
        final EObject favoriteList = resourceContents.get(0);
        if (!(favoriteList instanceof com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorites)) {
          return;
        }
        ((com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorites) favoriteList)
            .getObjectSearchFavorites()
            .forEach(fav -> favorites.addFavorite(fav));
      }
    } catch (final IllegalArgumentException | IOException e) {
      e.printStackTrace();
    }
  }

  /*
   * Creates option for loading/saving favorites via EMF
   */
  private static Map<String, Object> createEmfResourceOptions() {
    final HashMap<String, Object> options = new HashMap<>();
    options.put(XMLResource.OPTION_ENCODING, "UTF-8"); //$NON-NLS-1$
    options.put(XMLResource.OPTION_CONFIGURATION_CACHE, Boolean.TRUE);
    options.put(Resource.OPTION_SAVE_ONLY_IF_CHANGED,
        Resource.OPTION_SAVE_ONLY_IF_CHANGED_MEMORY_BUFFER);
    return options;
  }

  /*
   * Returns the favorites file location in the current workspace
   */
  private static String getFavoritesFilePath() {
    final IPath pluginWorkspacePath = Platform.getStateLocation(SearchAndAnalysisPlugin.getDefault()
        .getBundle());
    return pluginWorkspacePath.addTrailingSeparator().toOSString() + FAVORITES;
  }

}
