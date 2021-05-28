# This dockerfile, the compiled .jar and the applications.properties file
# have to be distributed in the same folder
FROM openjdk:11.0.9.1-jre-slim-buster

# Define some variables regarding the Matlab installation
ARG MCR_VERSION=R2020b
ARG MCR_VNUM=v99

RUN apt-get update && \
	apt-get install -y curl wget unzip xorg

# Install MATLAB runtime
RUN \
	mkdir /mcr-install && cd /mcr-install && \
	wget -nv https://ssd.mathworks.com/supportfiles/downloads/R2020b/Release/2/deployment_files/installer/complete/glnxa64/MATLAB_Runtime_R2020b_Update_2_glnxa64.zip && \
	unzip MATLAB_Runtime_R2020b_Update_2_glnxa64.zip && \
	./install -mode silent -agreeToLicense yes && \
	rm -Rf /mcr-install

ENV LD_LIBRARY_PATH /usr/local/MATLAB/MATLAB_Runtime/${MCR_VNUM}/runtime/glnxa64:/usr/local/MATLAB/MATLAB_Runtime/${MCR_VNUM}/bin/glnxa64:/usr/local/MATLAB/MATLAB_Runtime/${MCR_VNUM}/sys/os/glnxa64:/usr/local/MATLAB/MATLAB_Runtime/${MCR_VNUM}/extern/bin/glnxa64

WORKDIR ./

COPY *jar ./

EXPOSE 8080

#CMD ["java", "-jar", "*.jar"] # does not work with shell wildcards
CMD java -jar *.jar