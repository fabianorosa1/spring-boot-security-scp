namespace my.sample;
using { User, Country, managed } from '@sap/cds/common';

entity Advertisement: managed {
  key id : Integer;
  title  : String;
  contact  : String;
  cconfidentiality_level : String;
  version  : Integer;
}