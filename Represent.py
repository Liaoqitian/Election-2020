# Represent! 
#
# Simple example of using the Google GEO API and CIVIC API to lookup Senate and House Representatives
# Note: Does not handle all errors properly
#
# Uses: https://developers.google.com/civic-information
#       https://developers.google.com/maps/documentation/geocoding/overview
# 
#
# For CS160 Fall 2020
# Eric Paulos

import requests
import json
import random

# put your API key here
API_KEY = ""

CIVIC_URL = "https://www.googleapis.com/civicinfo/v2/representatives"
GEO_URL = "https://maps.googleapis.com/maps/api/geocode/json"

# example address to test with (i.e. Jacobs Hall)
ADDRESS = "2530 Ridge Rd, Berkeley, CA"

# crude way to set range of lat/lng for inside US – this is very imcomplete and better methods exist
LAT_MAX = 41.8
LAT_MIN = 33.8
LNG_MAX = -81.5
LNG_MIN = -116.2

def main():
	print("Represent!\n")
	# if you have a valid US postal address you can look up the representatives directly
	Representatives(ADDRESS)
	# if you have a valid zip code you can look up the representatives directly
	Representatives("10011")
	# you can get an address from a GEO location 
	gps = addressToLatLng("77836")
	address = LatLngToAddress(gps[0], gps[1])
	Representatives(address)
	# or pick random GEO locations within the US (this is an incomplete boundry)
	for i in range(1,3):
		randLat = random.uniform(LAT_MIN, LAT_MAX)
		randLng = random.uniform(LNG_MIN, LNG_MAX)
		address = LatLngToAddress(randLat, randLng)
		Representatives(address)


# Convert address to LAT/LNG 
def addressToLatLng(address):
	latlng = []
	# replace spaces with '+'
	place_url = "?address=" + address.replace(' ', '+')
	full_url = GEO_URL + place_url + "&key=" + API_KEY
	response = json.loads(requests.get(full_url).text)
	lat = response["results"][0]["geometry"]["location"]["lat"]
	lng = response["results"][0]["geometry"]["location"]["lng"]
	latlng.append(lat)
	latlng.append(lng)
	#print("%f %f" % (lat , lng))
	return latlng


# Convert LAT/LNG to postal address (if possible)
def LatLngToAddress(lat, lng):
	gps_url = "?latlng={lat_val},{lng_val}".format(lat_val = lat, lng_val = lng)
	full_url = GEO_URL + gps_url + "&key=" + API_KEY
	response = json.loads(requests.get(full_url).text)
	address = response["results"][0]["formatted_address"]
	return address

# Lookup Representatives for a given US address
def Representatives(address):
	# replace spaces with '+'
	place_url = "?address=" + address.replace(' ', '+')
	full_url = CIVIC_URL + place_url + "&key=" + API_KEY
	response = json.loads(requests.get(full_url).text)
	# print the city and state 
	print(response["normalizedInput"]["city"] + ", " + response["normalizedInput"]["state"])
	offices = response["offices"]
	officials = response["officials"]
	for i in range(0,len(offices)):
		if (offices[i]["name"] == "U.S. Senator"):
			print("U.S. Senators")
			index = offices[i]["officialIndices"]
			for j in range(0, len(index)):
				printPerson(officials[index[j]])
		elif(offices[i]["name"] == "U.S. Representative"):
			print("U.S. Representative")
			index = offices[i]["officialIndices"]
			for j in range(0, len(index)):
				printPerson(officials[index[j]])
	print(" ")


# Print name and other information for elected offical
def printPerson(person):
	print("   " + person["name"] + " [" + person["party"][0] + "] " + person["phones"][0] + " " +person["urls"][0])



if __name__ == "__main__":
    main()