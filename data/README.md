# Getting Geo Data

## Get data from OSM with Overpass API
```
[out:json][timeout:250];
(
  relation["type"="route"]["route"~"(train)|(light_rail)"]["network"~".*ZVV.*"]({{bbox}});
);
out body;
>;
out skel qt;
```


## GeoJSON to Shape File
```
ogr2ogr -nlt LINESTRING -skipfailures zvv_lines.shp zvv.geojson OGRGeoJSON
ogr2ogr -nlt POINT -skipfailures zvv_stations.shp zvv.geojson OGRGeoJSON
```

## Remove platforms
Using ArcGIS:
- Menu Selection, Select by Attribute
- `"@relations" LIKE '%platform%'`
- Editor Toolbar, Start Editing
- delete records using the attribute table
