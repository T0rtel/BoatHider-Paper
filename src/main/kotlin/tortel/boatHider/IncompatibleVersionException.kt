package tortel.gamer.BoatHider

class IncompatibleVersionException(version: String) :
    RuntimeException("Version $version incompatible with BoatHider plugin!")
