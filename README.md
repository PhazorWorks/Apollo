<h1 align="center">
  Apollo
</h1>

<p align="center">
  <a href="#overview">Overview</a>
  •
  <a href="#installation">Installation</a>
  •
  <a href="#requirements">Requirements</a>

</p>

# Overview

Apollo is a fully modular music bot – meaning all special features and commands can be enabled/disabled to your liking,
making it completely customizable.

# Installation

1) Clone the repo

```
git clone https://github.com/PhazorWorks/Apollo/
```

2) Rename docker config file

```
mv docker-compose.yml.example docker-compose.yml
```

3) Fill out the config
```
nano docker-compose.yml
```
4) Run the bot

```
docker-compose up -d
```

# Requirements
* [LavaLink](https://github.com/freyacodes/Lavalink)* [Sentry (Optional)](https://sentry.io/)
* [Image Generation API (Optional)](https://hub.docker.com/r/gigafyde/image-gen)
* [Spotify Web API (Optional)](https://hub.docker.com/r/gigafyde/spotify-web)
* [Lyrics Web API (Optional)](https://hub.docker.com/r/gigafyde/apollo-lyrics)
