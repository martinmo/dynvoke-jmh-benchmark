#!/usr/bin/env groovy

pipeline {
  agent any
    stages {
      stage("Tests") {
        steps {
          sh "./mvnw clean test"
        }
      }
    }
}

// vim:sw=2 ts=2 et
