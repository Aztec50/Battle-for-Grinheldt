<?xml version="1.0" encoding="UTF-8"?>
<tileset name="tiles" tilewidth="16" tileheight="16" tilecount="4" columns="4">
 <image source="../land_tiles/tiles.png" width="64" height="16"/>
 <tile id="0">
  <properties>
   <property name="moveCost" type="int" value="1"/>
   <property name="troopOn" type="bool" value="false"/>
   <property name="troopTeam" type="bool" value="false"/>
  </properties>
 </tile>
 <tile id="1">
  <properties>
   <property name="moveCost" type="int" value="2"/>
   <property name="troopOn" type="bool" value="false"/>
   <property name="troopTeam" type="bool" value="false"/>
  </properties>
 </tile>
 <tile id="2">
  <properties>
   <property name="moveCost" type="int" value="3"/>
   <property name="troopOn" type="bool" value="false"/>
   <property name="troopTeam" type="bool" value="false"/>
  </properties>
 </tile>
 <tile id="3">
  <properties>
   <property name="moveCost" type="int" value="-1"/>
   <property name="troopOn" type="bool" value="true"/>
   <property name="troopTeam" type="bool" value="false"/>
  </properties>
 </tile>
</tileset>
