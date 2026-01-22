import { ActivityIndicator, AppState, Image, StatusBar, StyleSheet, Text, useColorScheme, View } from 'react-native';
import {
  SafeAreaProvider,
  SafeAreaView,
} from 'react-native-safe-area-context';
import MapView, { Marker } from 'react-native-maps';
import { NativeModules } from 'react-native';
import { useEffect, useState } from 'react';

type userLocation = {
  latitude: number;
  longitude: number;
};

type nearbyPlaces = {
  id: string;
  name: string;
  latitude: number;
  longitude: number;
  distance: number;
};

function App() {

  const { LocationModule } = NativeModules;

  // check dark or light mode
  const isDarkMode = useColorScheme() === 'dark';

  const [location, setLocation] = useState<userLocation | null>(null);
  const [places, setPlaces] = useState<nearbyPlaces[]>([]);
  const [loading, setLoading] = useState<Boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const loadLocation = async () => {
    try {
      setLoading(true);

      const data = await LocationModule.getLocationAndPlaces();
      if (!data) return;

      setLocation(data.location);
      setPlaces(data.places);
      setError(null);
    } catch (e: any) {
      setError(e?.message ?? 'Failed to fetch location');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadLocation();

    const subscription = AppState.addEventListener('change', state => {
      if (state === 'active') {
        loadLocation();
      }
    });

    return () => subscription.remove();
  }, []);

  return (
    <SafeAreaProvider>
      <StatusBar barStyle={isDarkMode ? 'light-content' : 'dark-content'} />
      <SafeAreaView style={{ flex: 1 }}>

        {/* Loading screen */}
        {loading && (
          <View style={styles.container}>
            <ActivityIndicator size="large" />
            <Text style={styles.text}>Fetching your location…</Text>
          </View>
        )}

        {/* error if any */}
        {!loading && error && (
          <View style={styles.container}>
            <Text style={[styles.text, { color: 'red' }]}>
              {error}
            </Text>
          </View>
        )}

        {/* Map */}
        {!loading && location && (
          <MapView
            key={isDarkMode ? 'map-dark' : 'map-light'}
            userInterfaceStyle={isDarkMode ? 'dark' : 'light'}
            style={StyleSheet.absoluteFill}
            initialRegion={{
              latitude: location.latitude,
              longitude: location.longitude,
              latitudeDelta: 0.01,
              longitudeDelta: 0.01,
            }}
            showsUserLocation
            showsMyLocationButton
          >
            {/* Nearby places */}
            {places.map(place => (
              <Marker
                key={place.id}
                coordinate={{
                  latitude: place.latitude,
                  longitude: place.longitude,
                }}
                title={place.name}
                description={`${Math.round(place.distance)} meters away`}>
                <Image
                  source={isDarkMode ? require('./src/assets/marker_dark.png') : require('./src/assets/marker_light.png')}
                  style={{ width: 40, height: 40 }}
                  resizeMode="contain"
                />
              </Marker>
            ))}
          </MapView>
        )}
      </SafeAreaView>
    </SafeAreaProvider>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  text: {
    marginTop: 12,
    fontSize: 16,
  },
});

export default App;
